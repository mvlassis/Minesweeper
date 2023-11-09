package application;

import java.io.FileWriter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CreatePopup {
	
	@FXML
	private TextField createScenarioID;
	@FXML
	private TextField difficultyText;
	@FXML
	private TextField totalMinesText;
	@FXML
	private TextField hasHypermineText;
	@FXML
	private TextField totalTimeText;
	
	@FXML
	private Button cancelButton;
	@FXML
	protected void handleCancelButton(ActionEvent event) {
		Stage stage = (Stage) cancelButton.getScene().getWindow();
	    stage.close();
	}
	
	@FXML
	private Button submitCreateButton;
	@FXML
	protected void handleSubmitCreateButton(ActionEvent event) {
		
		try {
			FileWriter writer = new FileWriter("medialab/"+createScenarioID.getText());
		
			writer.write(difficultyText.getText() + '\n');
			writer.write(totalMinesText.getText() + '\n');
			writer.write(hasHypermineText.getText() + '\n');
			writer.write(totalTimeText.getText() + '\n');
			
			writer.close();
			
			Stage stage = (Stage) submitCreateButton.getScene().getWindow();
		    stage.close();
		    	    
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void display() {
		try {
			Stage popup = new Stage();
			Parent root = FXMLLoader.load(LoadPopup.class.getResource("fxml_popupcreate.fxml"));
			
			popup.initModality(Modality.APPLICATION_MODAL);
			popup.setTitle("Create a new scenario");		
			
			Scene scene = new Scene(root, 400, 250);	
			popup.setScene(scene);
			popup.showAndWait();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
