package application;
 
import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
 
public class MainController{
	
	private Minesweeper game;
	
	@FXML
	private Canvas canvas;
	
	@FXML
	private Text totalMinesText;
	@FXML
	private Text markedMinesText;
	@FXML
	private Text timeLeftText;
	
	private int totalTime;
	private Timeline timeline;
	private IntegerProperty timeLeft;
	private boolean gameStarted = false;
	private int padding = 0;
	private int cell_size = 10;
	
	private void redraw()
	{
		GraphicsContext context = canvas.getGraphicsContext2D();
		context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		markedMinesText.setText(String.valueOf(game.getMarkedMines()));

		if (game.getGridSize() == 9) {
			cell_size = 40;
			padding = 60;
		}
		else if (game.getGridSize() == 16) {
			cell_size = 20;
			padding = 80;
		}
		for (int i = 0; i < game.getGridSize(); i++) {
			for (int j = 0; j < game.getGridSize(); j++) {
				//System.out.println("ye");
				int revealStatus = game.getRevealStatus(i, j);
				int cellType = game.getCell(i, j);
				if (revealStatus == Minesweeper.HIDDEN) {
					context.setFill(Color.DARKGREY);
				}
				else if (revealStatus == Minesweeper.REVEALED) {
					if (cellType >= Minesweeper.EMPTY) {
						context.setFill(Color.LIGHTGREY);
					}
					else if (cellType == Minesweeper.MINE) {
						context.setFill(Color.RED);
					}
					else if (cellType == Minesweeper.HYPERMINE) {
						context.setFill(Color.PURPLE);
					}
				}
				context.fillRect(j * cell_size + padding, i * cell_size, cell_size, cell_size);
				if (revealStatus == Minesweeper.REVEALED && cellType > Minesweeper.EMPTY) {
					context.setTextAlign(TextAlignment.CENTER);
			        context.setTextBaseline(VPos.CENTER);
			        context.setFill(Color.BLACK);
			        context.setFont(new Font(16));
					context.fillText(String.valueOf(cellType), (double)j * cell_size + padding + cell_size/2, (double)i * cell_size + cell_size/2);
					//Text text = new Text();
					//text.setText(String.valueOf(cellType));
					//text.setX((double)i * cell_size);
					//text.setY((double)j * cell_size + padding);
				}
				int hyperminedStatus = game.getHyperminedStatus(i, j);
				if (revealStatus == Minesweeper.REVEALED && cellType < Minesweeper.EMPTY && hyperminedStatus == Minesweeper.HYPERMINED) { 
					context.setTextAlign(TextAlignment.CENTER);
			        context.setTextBaseline(VPos.CENTER);
			        context.setFill(Color.BLACK);
			        if (game.getGridSize ()== 9) {
			        	context.setFont(new Font(16));
			        }
			        else {
			        	context.setFont(new Font(12));
			        }
					context.fillText("X", (double)j * cell_size + padding + 3 * cell_size / 4, (double)i * cell_size + 3 * cell_size / 4);
				}
				int markStatus = game.getMarkStatus(i, j);
				if (revealStatus == Minesweeper.HIDDEN && markStatus == Minesweeper.MARKED) {
					context.setTextAlign(TextAlignment.CENTER);
			        context.setTextBaseline(VPos.CENTER);
			        context.setFill(Color.BLUE);
			        context.setFont(new Font(22));
					context.fillText("M", (double)j * cell_size + padding + cell_size/2, (double)i * cell_size + cell_size/2);
				}
			}
		}
		if (game.getGameStatus() == Minesweeper.GAME_LOST) {
			context.setTextAlign(TextAlignment.CENTER);
	        context.setTextBaseline(VPos.CENTER);
	        context.setFill(Color.BLACK);
	        context.setFont(new Font(50));
			context.fillText("Game Over!", canvas.getWidth()/2, canvas.getHeight()/2);
		}
		if (game.getGameStatus() == Minesweeper.GAME_WON) {
			context.setTextAlign(TextAlignment.CENTER);
	        context.setTextBaseline(VPos.CENTER);
	        context.setFill(Color.GREEN);
	        context.setFont(new Font(50));
			context.fillText("Game Won!", canvas.getWidth()/2, canvas.getHeight()/2);
		}
		if (game.getGameStatus() != Minesweeper.GAME_PLAYING) {
			if (timeline != null) {
				timeline.stop();
			}	
			game.saveGame(timeLeftText.getText());
		}
	}
	
	@FXML
	protected void handleMouseMove(MouseEvent event) {
		if (gameStarted && game.getGameStatus() == Minesweeper.GAME_PLAYING) {
			redraw();
			int x = (int)event.getX();
			int y = (int)event.getY();
		
			int i = (int)y / cell_size;
			int j = (int)(x - padding) / cell_size;

			if (i < game.getGridSize() && i >= 0 && j < game.getGridSize() && j >= 0 && x >= padding && game.getRevealStatus(i, j) != Minesweeper.REVEALED)
			{
				GraphicsContext context = canvas.getGraphicsContext2D();
				context.setFill(Color.CHOCOLATE);
				context.fillRect(j * cell_size + padding,  i * cell_size,  cell_size,  cell_size);
			}
			//System.out.println(i + " " + j);
		}
	}
	
	@FXML
	protected void handleMouseClick(MouseEvent event) {
		if (gameStarted && game.getGameStatus() == Minesweeper.GAME_PLAYING) {
			if (event.getButton() == MouseButton.PRIMARY)
				{
					int x = (int)event.getX();
					int y = (int)event.getY();
			
					int i = (int)y / cell_size;
					int j = x < padding ? -1 :(int)(x - padding) / cell_size;
			
					game.click(i, j);
				}
				
		
				else if (event.getButton() == MouseButton.SECONDARY)
				{
					int x = (int)event.getX();
					int y = (int)event.getY();
		
					int i = (int)y / cell_size;
					int j = x < padding ? -1 :(int)(x - padding) / cell_size;
			
					game.mark(i, j);
			}
			redraw();
		}
	}
	
	@FXML
	private MenuItem createButton;
	@FXML
	protected void handleCreateButton(ActionEvent event) {
		CreatePopup.display();
	}
	
	@FXML 
	private MenuItem loadButton;
	@FXML 
	protected void handleLoadButton(ActionEvent event) {
		int[] parameters = new int[4];
		parameters = LoadPopup.display();
		if (parameters == null) 
		{
			return;
		}

		if (parameters[0] == 1) {
			parameters[0] = 9;
		}
		else if (parameters[0] == 2) {
			parameters[0] = 16;
		}
		game = new Minesweeper(parameters[0], parameters[1], parameters[2], parameters[3]);	
	
		totalMinesText.setText(String.valueOf(parameters[1]));
		timeLeft = new SimpleIntegerProperty(parameters[2]);
		timeLeftText.textProperty().bind(timeLeft.asString());
		totalTime = parameters[2];
		redraw();
	}
	
	@FXML 
	private MenuItem startButton;
	@FXML
	protected void handleStartButton(ActionEvent event) {
		int parameter0 = game.getGridSize();
		int parameter1 = game.getTotalMines();
		int parameter2 = totalTime;
		int parameter3 = game.hasHypermine();
		game = new Minesweeper(parameter0, parameter1, parameter2, parameter3);	
		redraw();
		gameStarted = true;
		timeLeft.set(totalTime);
		timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(totalTime+1), new KeyValue(timeLeft, 0)));
		timeline.playFromStart();
		timeline.setOnFinished(event2 -> {game.setLost(); redraw();});
		game.saveMinefield("medialab/mines.txt");
	}
	
    @FXML private MenuItem exitButton;
    @FXML protected void handleExitButton(ActionEvent event) {
    	Platform.exit();
    }
    
    @FXML private MenuItem roundsButton;
    @FXML protected void handleRoundsButton(ActionEvent event) {
    	Stage popup = new Stage();
		try {
			Parent root = FXMLLoader.load(LoadPopup.class.getResource("fxml_popuprounds.fxml"));
			popup.initModality(Modality.APPLICATION_MODAL);
			popup.setTitle("Results of previous rounds");
			Scene scene = new Scene(root, 720, 700);	
			popup.setScene(scene);
			popup.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}	
    }
    
    @FXML private MenuItem solutonButton;
    @FXML protected void handleSolutionButton(ActionEvent event) {
    	game.revealSolution();
    	redraw();
    }

}