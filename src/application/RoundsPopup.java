package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RoundsPopup implements Initializable {
	@FXML private Text text00;
	@FXML private Text text01;
	@FXML private Text text02;
	@FXML private Text text03;
	
	@FXML
	private Text text10;
	@FXML
	private Text text11;
	@FXML
	private Text text12;
	@FXML
	private Text text13;
	
	@FXML
	private Text text20;
	@FXML
	private Text text21;
	@FXML
	private Text text22;
	@FXML
	private Text text23;
	
	@FXML
	private Text text30;
	@FXML
	private Text text31;
	@FXML
	private Text text32;
	@FXML
	private Text text33;
	
	@FXML
	private Text text40;
	@FXML
	private Text text41;
	@FXML
	private Text text42;
	@FXML
	private Text text43;
	
	private Text[] textArray;
	
	@FXML
	private Button OKButton;
	@FXML
	protected void handleOKButton(ActionEvent event) {
		Stage stage = (Stage) OKButton.getScene().getWindow();
	    stage.close();
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		text40.setText("deez");
		textArray = new Text[20];
		textArray[0] = text40;
		textArray[1] = text41;
		textArray[2] = text42;
		textArray[3] = text43;
		textArray[4] = text30;
		textArray[5] = text31;
		textArray[6] = text32;
		textArray[7] = text33;
		textArray[8] = text20;
		textArray[9] = text21;
		textArray[10] = text22;
		textArray[11] = text23;
		textArray[12] = text10;
		textArray[13] = text11;
		textArray[14] = text12;
		textArray[15] = text13;
		textArray[16] = text00;
		textArray[17] = text01;
		textArray[18] = text02;
		textArray[19] = text03;
		
		int lines = 0;
	    try {

	        // much slower, this task better with sequence access
	        //lines = Files.lines(path).parallel().count();
	    	Path path = Paths.get("medialab/savedgames.txt");
	        lines = (int)Files.lines(path).count();
	        File file = new File("medialab/savedgames.txt");
	        Scanner scanner = new Scanner(file);
	          
	        int text_index = 0;
	        if (lines > 5) {
	        	for (int i = 0; i < lines - 5; i++) {
	        		scanner.nextLine();
	        	}
	        	
	        }
	        else {
	        	text_index = 4 * (5 - lines);
	        }
	        while (scanner.hasNextLine() && scanner.hasNextInt()) {
	        	//String line = scanner.nextLine();
	        	//System.out.println(line);
	        	String mines = String.valueOf(scanner.nextInt());
	        	String totalClicks = String.valueOf(scanner.nextInt());
	        	String totalTime = String.valueOf(scanner.nextInt());
	        	String result = String.valueOf(scanner.nextInt()).equals(String.valueOf(Minesweeper.GAME_LOST)) ? "Game Lost" : "Game Won!"; 

	        	textArray[text_index].setText(mines);
	        	textArray[text_index+1].setText(totalClicks);
	        	textArray[text_index+2].setText(totalTime);
	        	textArray[text_index+3].setText(result);
	        	
	        	text_index += 4;
	        }
	        scanner.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }  
	}
	

	
}
