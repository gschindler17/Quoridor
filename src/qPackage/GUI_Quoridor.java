package qPackage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


public class GUI_Quoridor extends Application implements PropertyChangeListener, EventHandler<ActionEvent>, ChangeListener<String>{


	
	private GridPane root;
	
	
	/**
	 * Model that is the "brains" of the program
	 * Class: QuoridorBoardModel
	 */
	private QuoridorBoardModel boardLogic;
	
	
	/**
	 * Determines the size of the game board
	 */
	private ComboBox<Integer> setSizeCB;
	
	
	/**
	 * Stores all of the buttons for the game board
	 */
	private GridPane gameboardPane;
	
	
	/**
	 * Resets the game
	 */
	private Button resetButton;
	
	
	private Label currentTurnLabel;
	
	
	public GUI_Quoridor() {
		boardLogic = new QuoridorBoardModel();
		
		boardLogic.addPropertyChangeListener(this);
	}
	
	@Override
	public void start(Stage primaryStage){
		
		
		
		try {
			
			root = new GridPane();
			Scene scene = new Scene(root, 800, 700);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Quoridor");
			
			currentTurnLabel = new Label("It is currently Player " + boardLogic.getCurrentPlayer() + "'s turn");
			currentTurnLabel.setPadding(new Insets(20));
			
			
			resetButton = new Button("Clear");
			resetButton.setOnAction(this);
			
			
			setSizeCB = new ComboBox<Integer>();
			setSizeCB.getItems().addAll(3,5,7,9,11);
			setSizeCB.setOnAction(this);
			
			
			updateGameBoard();
			
			
			
			root.add(setSizeCB, 0, 1);
			root.add(currentTurnLabel, 1, 1);
			root.add(resetButton, 0, 3);
			
			primaryStage.show();
			
			
		} catch (Exception _exception) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
	    		alert.setTitle("Something doesn't look right...");
	    		alert.setContentText(_exception.getMessage());
	    		alert.showAndWait();
		}
		
	}
	
	
	public void updateGameBoard()
	{
		int[][] gameBoard = boardLogic.getGameBoard();
		
		
		gameboardPane = new GridPane();
		
		
		for (int row = 0; row < gameBoard.length; row++)
		{
			for(int col = 0; col < gameBoard[0].length; col++)
			{
				
				System.out.print(gameBoard[row][col] + ", ");
				
				if (boardLogic.isBarrier(row, col) && !(boardLogic.isNullBarrier(row, col)))
				{
					SmartButton temp = new SmartButton(row, col);
					temp.setOnAction(this);
					if(boardLogic.passable(row, col))
					{
						temp.setStyle("-fx-background-color: #9cbff7; ");
					}
					else
					{
						temp.setStyle("-fx-background-color: #000000; ");
					}
			
					if (boardLogic.isHorizontalBarrier(row, col))
					{
						temp.setPrefSize(100, 10);						
					}
					else if (boardLogic.isVerticalBarrier(row, col))
					{
						temp.setPrefSize(20, 100);
					}
					
					gameboardPane.add(temp, col, row);
				}
				
				else if (boardLogic.isPlayerSquare(row, col))
				{
					SmartButton temp = new SmartButton(row, col);
					temp.setOnAction(this);
					temp.setPrefSize(100, 100);
					
					if(boardLogic.occupiedPlayer1(row, col))
					{
						temp.setStyle("-fx-background-color: #ff0000; ");
					
					}
					else if(boardLogic.occupiedPlayer2(row, col))
					{
						temp.setStyle("-fx-background-color: #FFD700; ");
					}
					
					gameboardPane.add(temp, col, row);
				}
				
				
				
			}
			
			System.out.println();
		}
		
		root.add(gameboardPane, 1, 2);
	}
	
	public void updateFeedback() {
		root.getChildren().remove(currentTurnLabel);
		currentTurnLabel.setText("It is currently Player " + boardLogic.getCurrentPlayer() + "'s turn");
		root.add(currentTurnLabel, 1, 1);
	}
	
	@Override
	public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handle(ActionEvent event) {
		if (event.getSource() == setSizeCB)
		{
			Integer size = setSizeCB.getSelectionModel().getSelectedItem();
			boardLogic.setBoardSize(size);
		}
		
		if (event.getSource() == resetButton)
		{
			boardLogic.resetBoard();
		}
		
		
		
		try {
			if (event.getSource() instanceof SmartButton)
			{
				SmartButton selectedButton = (SmartButton) event.getSource();
				
				boardLogic.completeMove(selectedButton.row, selectedButton.col);
			}
		}catch (Exception _exception) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
    		alert.setTitle("Something doesn't look right...");
    		alert.setContentText(_exception.getMessage());
    		alert.showAndWait();
		}
		
	}

	
	
	
	
	
//---------------------------------------------------------------------------------------------------
//----------------------------CONTROLLER CODE--------------------------------------------------------
//---------------------------------------------------------------------------------------------------
	
	
public void propertyChange(PropertyChangeEvent evt) {
		
	
		if (evt.getPropertyName().equals("setSize"))
		{
			gameboardPane.getChildren().clear();
			this.updateGameBoard();
		}
		if (evt.getPropertyName().equals("nextTurn"))
		{
			System.out.println("NEXT TURN\n\n");
			gameboardPane.getChildren().clear();
			this.updateGameBoard();
			this.updateFeedback();
		}
		
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
