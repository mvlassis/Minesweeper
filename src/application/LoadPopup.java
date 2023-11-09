package application;

import java.io.File;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoadPopup {
	
	private static int[] gameParameters;
	
	@FXML private TextField loadScenarioID;
	
	@FXML
	private Button cancelButton;
	@FXML
	protected void handleCancelButton(ActionEvent event) {
		Stage stage = (Stage) cancelButton.getScene().getWindow();
	    stage.close();
	}
	
	@FXML private Button submitLoadButton;
	@FXML protected void handleSubmitLoadButton(ActionEvent event) {
		try {
			File file = new File("medialab/"+loadScenarioID.getText());
			Scanner scanner = new Scanner(file);
		
			int difficulty = 0;
			int mines = 0;
			int time = 0;
			int hyper_mine = 0;
			if (scanner.hasNextInt()) {
				difficulty = scanner.nextInt();	
			}
			else {
				scanner.close();
				throw new InvalidDescriptionException("Difficulty not found on this scenario!");
			}
			if (scanner.hasNextInt()) {
				mines = scanner.nextInt();	
			}
			else {
				scanner.close();
				throw new InvalidDescriptionException("Number of mines not found on this scenario!");
			}
			if (scanner.hasNextInt()) {
				time = scanner.nextInt();
				
			}
			else {
				scanner.close();
				throw new InvalidDescriptionException("Total time not found on this scenario!");
			}
			if (scanner.hasNextInt()) {
				hyper_mine = scanner.nextInt();	
			}
			else {
				scanner.close();
				throw new InvalidDescriptionException("Hypermine description not found on this scenario!");
			}
			
			// Check that all parameters are within their correct ranges
			if (difficulty != 1 && difficulty != 2) {
				scanner.close();
				throw new InvalidValueException("Difficulty not within the accepted range!");
			}
			if ((difficulty == 1 && (mines < 9 || mines > 11)) || (difficulty == 2 && (mines < 35 || mines > 45))) {
				scanner.close();
				throw new InvalidValueException("Total mines not within the accepted range!");
			}
			if ((difficulty == 1 && (time < 120 || time > 180)) || (difficulty == 2 && (time < 240 || time > 360))) {
				scanner.close();
				throw new InvalidValueException("Total time not within the accepted range!");
			}
			if ((difficulty == 1 && hyper_mine == 1) || (difficulty == 2 && hyper_mine == 0)) {
				scanner.close();
				throw new InvalidValueException("Hypermine not within the accepted range!");
			}
			//System.out.println(difficulty+ " " + mines + " " + time + " " + hyper_mine);
			gameParameters = new int[4];
			gameParameters[0] = difficulty;
			gameParameters[1] = mines;
			gameParameters[2] = time;
			gameParameters[3] = hyper_mine;
		
			scanner.close();
			
			Stage stage = (Stage) submitLoadButton.getScene().getWindow();
		    stage.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static int[] display() {
		
		try {
			Stage popup = new Stage();
			Parent root = FXMLLoader.load(LoadPopup.class.getResource("fxml_popupload.fxml"));
			
			popup.initModality(Modality.APPLICATION_MODAL);
			popup.setTitle("Load a new scenario");		
			
			Scene scene = new Scene(root, 300, 200);	
			popup.setScene(scene);
			popup.showAndWait();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return gameParameters;
	}
	
	
}
