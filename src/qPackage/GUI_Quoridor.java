package qPackage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
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
	
	
	public GUI_Quoridor() {
		boardLogic = new QuoridorBoardModel();
		
		boardLogic.addPropertyChangeListener(this);
	}
	
	@Override
	public void start(Stage primaryStage){
		
		
		
		try {
			
			root = new GridPane();
			Scene scene = new Scene(root, 1440, 1080);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Quoridor");
			
			
	
			
			
			resetButton = new Button("Clear");
			resetButton.setOnAction(this);
			
			
			setSizeCB = new ComboBox<Integer>();
			setSizeCB.getItems().addAll(3,5,7,9,11);
			setSizeCB.setOnAction(this);
			
			
			updateGameBoard();
			
			
			
			root.add(setSizeCB, 0, 1);
			root.add(resetButton, 0, 2);
			
			primaryStage.show();
			
			
		} catch (Exception e) {
			e.printStackTrace();
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
				
				
				if (boardLogic.isHorizontalBarrier(row, col))
				{
					Button temp = new Button();
					temp.setPrefSize(100, 10);
					temp.setMaxHeight(8);
					gameboardPane.add(temp, col, row);
					
					if(boardLogic.passable(row, col))
					{
						temp.setStyle("-fx-background-color: #DCDCDC; ");
					}
					else
					{
						temp.setStyle("-fx-background-color: #000000; ");
					}
				}
				else if (boardLogic.isVerticalBarrier(row, col))
				{
					
					Button temp = new Button();
					temp.setPrefSize(10, 100);
					gameboardPane.add(temp, col, row);
						
	
					if(boardLogic.passable(row, col))
					{
						temp.setStyle("-fx-background-color: #DCDCDC; ");
					}
					else
					{
						temp.setStyle("-fx-background-color: #000000; ");
					}
				}
				
				else if (boardLogic.isPlayerSquare(row, col))
				{
					if(boardLogic.occupiedPlayer1(row, col))
					{
						Button temp = new Button();
						temp.setStyle("-fx-background-color: #ff0000; ");
						temp.setPrefSize(100, 100);
						gameboardPane.add(temp, col, row);
						
						
					}
					else if(boardLogic.occupiedPlayer2(row, col))
					{
						Button temp = new Button();
						temp.setStyle("-fx-background-color: #FFD700; ");
						temp.setPrefSize(100, 100);
						gameboardPane.add(temp, col, row);
					}
					else
					{
						Button temp = new Button();
						temp.setPrefSize(100, 100);
						gameboardPane.add(temp, col, row);
					}
				}
				
				
				
			}
			
			System.out.println();
		}
		
		root.add(gameboardPane, 2, 2);
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
		if (evt.getPropertyName().equals("setValueAt"))
		{
			
		}
		
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
