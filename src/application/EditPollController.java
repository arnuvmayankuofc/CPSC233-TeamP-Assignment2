package application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import model.InvalidPartyDataException;
import model.Party;
import model.Poll;

/**
 * EditPollController - the controller for the Edit Poll View. Lets the user
 * choose a poll to edit, then a party in that poll, and allows them to input a
 * projected number of seats and percentage vote won, then updates that party.
 * 
 * @author Arnuv Mayank
 */
public class EditPollController extends PollTrackerController {

	// these two private variables are what allow the class to edit the database
	// created by factory
	private Poll pollToUpdate;
	private Party partyToUpdate;
	// this is to ensure that the listener is only created once
	private boolean listenerCreated = false;

	@FXML
	private Button updatePartyButton;

	@FXML
	private Label pollEditText;

	@FXML
	private Label projectedNumSeatsText;

	@FXML
	private ChoiceBox<String> partyToUpdateChoice;

	@FXML
	private Label partyToUpdateText;

	@FXML
	private Label percent;

	@FXML
	private TextField projectedNumSeats;

	@FXML
	private Label totalSeats;

	@FXML
	private Label resultMessageBox;

	@FXML
	private Button clearButton;

	@FXML
	private ChoiceBox<String> pollChoices;

	@FXML
	private Label projVotePercentageText;

	@FXML
	private Label slash;

	@FXML
	private TextField projVotePercentage;

	@FXML
	private Label pollListNotMadeMessage;

	/**
	 * shouldDisplayElements - hides all the elements but the error message that the
	 * user hasn't created a poll list if that is the case, otherwise it shows
	 * everything but said error message
	 */
	private void shouldDisplayElements(boolean hide) {
		updatePartyButton.setVisible(hide);
		pollEditText.setVisible(hide);
		projectedNumSeatsText.setVisible(hide);
		partyToUpdateText.setVisible(hide);
		partyToUpdateChoice.setVisible(hide);
		percent.setVisible(hide);
		projectedNumSeats.setVisible(hide);
		totalSeats.setVisible(hide);
		resultMessageBox.setVisible(hide);
		clearButton.setVisible(hide);
		pollChoices.setVisible(hide);
		projVotePercentageText.setVisible(hide);
		slash.setVisible(hide);
		projVotePercentage.setVisible(hide);

		pollListNotMadeMessage.setVisible(!hide);
	}

	/**
	 * clearCurrentInfo - clears all the user inputed data so they can and input
	 * again
	 */
	private void clearCurrentInfo() {
		/*
		 * The first 4 objects cleared are those in which the user has a choice The last
		 * one is the result message box, which notifies the user of the result of their
		 * update attempt.
		 */
		partyToUpdateChoice.getSelectionModel().clearSelection();
		pollChoices.getSelectionModel().clearSelection();
		projectedNumSeats.clear();
		projVotePercentage.clear();
		resultMessageBox.setText("");
	}

	/**
	 * clearEverything - clears all pertinent objects on the Scene of their contents
	 */
	private void clearEverything() {
		/*
		 * this method clears all of the current info with the above method, then also
		 * clears all of the poll options and party options. The reason for this is that
		 * the poll and party options are generated in refresh, so they'll get
		 * duplicated without clearing them first if you were to switch to another view
		 * then switch back to this view.
		 */
		clearCurrentInfo();
		partyToUpdateChoice.getItems().removeAll(partyToUpdateChoice.getItems());
		pollChoices.getItems().removeAll(pollChoices.getItems());
	}

	/**
	 * initializePartyOptions - once the user has decided a poll to edit, the party
	 * options are generated
	 */
	private void initializePartyOptions() {
		Party[] parties = pollToUpdate.getPartiesSortedBySeats();
		// this array contains the toString value of the party names (i.e. with %vote
		// and #seats)
		String partyNamesToDisplay[] = new String[pollToUpdate.getNumberOfParties()];

		partyToUpdateChoice.getSelectionModel().clearSelection();
		partyToUpdateChoice.getItems().removeAll(partyToUpdateChoice.getItems());
		// the party to update must be reset so that the party options can be reset and
		// the user can pick
		partyToUpdate = null;
		// collects the party names in toString form, then adds them to the dropdown
		for (int i = 0; i < pollToUpdate.getNumberOfParties(); i++) {
			Party party = parties[i];
			partyNamesToDisplay[i] = party.toString();
		}
		partyToUpdateChoice.setItems(FXCollections.observableArrayList(partyNamesToDisplay));
	}

	/**
	 * Makes the result box text red and displays a specified error message
	 * 
	 * @param message - the error message
	 */
	private void displayErrorMessage(String message) {
		resultMessageBox.setTextFill(Paint.valueOf("red"));
		resultMessageBox.setText(message + "Please try again. ");
	}
	
	/**
	 * initialize - does nothing due to the design of this project; initialize is
	 * called before the polls are created, so it is unusable.
	 */
	@FXML
	void initialize() {

	}

	/**
	 * clearInfo - this is the clear button, and only clears the current info upon
	 * click
	 * 
	 * @param event - the click
	 */
	@FXML
	void clearInfo(ActionEvent event) {
		clearCurrentInfo();
	}

	/**
	 * updatePartyInfo - this is the update button, and updates the poll/party info
	 * based on the user's specifications upon click
	 * 
	 * @param event - the click
	 */
	@FXML
	void updatePartyInfo(ActionEvent event) {
		String invalidVotesError = "Percentage of votes must be between 0 and 1 (inclusive). ";
		String invalidSeatsError = "Number of seats must be non-negative. ";
		/*
		 * There are many things that can go wrong with the user input: 1) they don't
		 * select a poll 2) they don't select a party 3) they input an invalid number of
		 * seats/no number at all 4) they input an invalid percentage/no number at all
		 * All of these are handled in the try-catch block
		 */
		try {
			int projectedNumSeatsInt = Integer.parseInt(projectedNumSeats.getText());
			float projectedVote = Float.parseFloat(projVotePercentage.getText()) / 100f;

			partyToUpdate.setProjectedNumberOfSeats(projectedNumSeatsInt);
			partyToUpdate.setProjectedPercentageOfVotes(projectedVote);
			resultMessageBox.setText("Updated successfully!");
			// successful message is green, error is red
			resultMessageBox.setTextFill(Paint.valueOf("green"));
		} catch (NullPointerException npe) {
			// case when they don't select a poll/party
			displayErrorMessage("You did not select a party/poll to edit. ");
		} catch (NumberFormatException nfe) {
			// case where the votes/seats they enter is not a number
			displayErrorMessage("One of the fields inputted is not a number. ");
		} catch (InvalidPartyDataException ipde) {
			// case where the votes/seats they enter is a number but is not valid based on
			// specifications in assignment
			if (ipde.getMessage().equals(invalidVotesError)) {
				displayErrorMessage("Percentage of votes must be between 0% and 100%. ");
			} else if (ipde.getMessage().equals(invalidSeatsError)) {
				displayErrorMessage(invalidSeatsError);
			} else {
				displayErrorMessage("Invalid. ");
			}
		}
	}

	/**
	 * refresh - a definition of the abstract method in PollTrackerController This
	 * is called any time the view is changed so that the information can be
	 * updated, but this will serve as the "runner" of the class since initialize is
	 * unusable
	 */
	@Override
	public void refresh() {
		// if there's no poll list, they can't edit a poll
		if (getPollList() != null) {
			// TODO Auto-generated method stub
			shouldDisplayElements(true);
			clearEverything();
			Poll[] polls = getPollList().getPolls();
			String[] pollNames = new String[polls.length];

			totalSeats.setText("" + getPollList().getNumOfSeats());
			// collects the poll names, then adds them to the dropdown
			for (int i = 0; i < polls.length; i++) {
				pollNames[i] = polls[i].getPollName();
			}

			pollChoices.setItems(FXCollections.observableArrayList(pollNames));

			// ensures listener is only created once rather than on every refresh
			if (!listenerCreated) {

				listenerCreated = true;
				/*
				 * Each dropdown menu has a change listener -- the poll choices' listener is so
				 * that the party dropdown knows which parties to grab, and the party choices'
				 * listener is so that the specific party to update is grabbed.
				 */
				pollChoices.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue observable, Number oldValue, Number newValue) {
						// a value of -1 indicates that the scene is on a different view than this one
						if (newValue.intValue() >= 0) {
							// after selecting a new poll, reset the result of the last edit attempt
							resultMessageBox.setText("");
							// can't use polls because the event listener is triggered before polls is
							// updated.
							pollToUpdate = getPollList().getPolls()[newValue.intValue()];
							initializePartyOptions();
						}
					}
				});
				partyToUpdateChoice.getSelectionModel().selectedIndexProperty()
						.addListener(new ChangeListener<Number>() {
							@Override
							public void changed(ObservableValue observable, Number oldValue, Number newValue) {
								// a value of -1 indicates that the scene is on a different view than this one,
								// hence this if
								if (newValue.intValue() >= 0) {
									partyToUpdate = pollToUpdate.getPartiesSortedBySeats()[newValue.intValue()];
								}
							}
						});
			}
		} else {
			shouldDisplayElements(false);
		}
	}

}
