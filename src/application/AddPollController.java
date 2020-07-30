/** 
 * AddPollController - the controller for the Add Poll View. 
 * The user can provide a name and the placement for each of the polls that 
 * the application will track.  
 * 
 * @author Michaela Kasongo, 30041372
 * @version 3.0, July 27, 2020.
 */

package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Poll;
import model.PollList;

public class AddPollController extends PollTrackerController {
	@FXML
	private ChoiceBox<String> PollPlacementChoice;

	@FXML
	private Button AddPollButton;

	@FXML
	private Button clearButton;

	@FXML
	private TextField UserInput;

	@FXML
	private Label PollNameText;

	@FXML
	private Label PollPlacement;

	@FXML
	private Label errorMessage;

	/**
	 * shouldDisplay - hides all the elements but the error message that the user
	 * hasn't created a poll list if that is the case, otherwise it shows everything
	 * but said error message
	 */
	private void shouldDisplayElements(boolean hide) {
		PollPlacementChoice.setVisible(hide);
		AddPollButton.setVisible(hide);
		clearButton.setVisible(hide);
		UserInput.setVisible(hide);
		PollNameText.setVisible(hide);
		PollPlacement.setVisible(hide);

		errorMessage.setVisible(!hide);
	}

	@FXML
	/**
	 * ClearInfo - this is the clear button, and only clears the current info(text
	 * field and choiceBox options) upon click.
	 * 
	 * @param event - the click
	 *
	 */
	void ClearInfo(ActionEvent event) {
		clear();
	}

	@FXML
	/**
	 * clear- clears all pertinent objects on the Scene of their contents
	 */

	void clear() {
		/*
		 * The first 2 objects cleared are those in which the user has a choice The last
		 * one, UserInput, validates their input into the text field.
		 */
		PollPlacementChoice.getSelectionModel().clearSelection();
		UserInput.clear();
		UserInput.setText("");

	}

	/**
	 * AddPoll - this is the add poll button. This should result in the creation of
	 * a new poll in the applications list of polls with some default information
	 * for all the parties in the election.
	 * 
	 * @param event - the click
	 */
	@FXML
	void AddPoll(ActionEvent event) {
		// get polls and create a random poll with the user input name provided.
		Poll[] poll = getPollList().getPolls();
		Poll RandomPoll = getFactory().createRandomPoll(UserInput.getText());

		// get index of the drop down options and set as index of random Poll
		int index = PollPlacementChoice.getSelectionModel().getSelectedIndex();
		poll[index] = RandomPoll;

		// add new random poll to the poll List along with default party information
		PollList finallist = new PollList(poll.length, getPollList().getNumOfSeats());
		for (int i = 0; i < poll.length; i++) {
			finallist.addPoll(poll[i]);
		}
		setPollList(finallist); // set list
	}

	/**
	 * refresh - a definition of the abstract method in PollTrackerController This
	 * is called any time the view is changed so that the information can be
	 * updated, but this will serve as the "runner" of the class since initialize is
	 * unusable.
	 */
	@Override
	public void refresh() {
		// if there's no poll list, they can't add a poll
		if (getPollList() != null) {
			shouldDisplayElements(true);
			// clears user input and choice box with every refresh.
			clear();
			// collects poll names and sets them as the drop down in the ChoiceBox.
			PollPlacementChoice.getItems().removeAll(PollPlacementChoice.getItems());
			for (Poll poll : getPollList().getPolls()) {
				PollPlacementChoice.getItems().add("replace" + " " + poll.getPollName());
			}
		} else {
			shouldDisplayElements(false);
		}
	}
}