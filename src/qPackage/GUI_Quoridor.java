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
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


public class GUI_Quoridor extends Application implements PropertyChangeListener, EventHandler<ActionEvent>, ChangeListener<String>{

	final int PASSABLE = 0;
	final int BLOCKED = 1;
	
	
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
			
			GridPane root = new GridPane();
			Scene scene = new Scene(root, 1100, 700);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Quoridor");
			
			
	
			
			
			resetButton = new Button("Clear");
			resetButton.setOnAction(this);
			
			
			setSizeCB = new ComboBox<Integer>();
			setSizeCB.getItems().addAll(1,2,3,4,5,6,7);
			setSizeCB.setOnAction(this);
			
			
			updateGameBoard();
			
			
			root.add(gameboardPane, 2, 2);
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
				if (boardLogic.isHorizontalBarrier(row, col))
				{
					if(gameBoard[row][col] == PASSABLE)
					{
						Button temp = new Button("Barrier");
						temp.setMaxSize(100, 10);
						gameboardPane.add(temp, row, col);
					}
				}
			}
		}
		
	}
	
	
	
	@Override
	public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handle(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
	
	




	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
}
