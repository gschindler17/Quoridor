package qPackage;

import java.beans.PropertyChangeEvent;

public class Controller {

	/**
	 * Model that is the "brains" of the program
	 * Class: QuoridorBoardModel
	 */
	private QuoridorBoardModel boardLogic;
	
	private GUI_Quoridor gui;
	
	
	
	public Controller(String[] args)
	{
		boardLogic = new QuoridorBoardModel();
		
		gui = new GUI_Quoridor();
		
		gui.start(args);
	}
	
	
	
	public void propertyChange(PropertyChangeEvent evt) {
		
		if (evt.getPropertyName().equals("setSize"))
		{
			
		}
		if (evt.getPropertyName().equals("setValueAt"))
		{
			
		}
		
	}
	
	
	public static void main(String[] args) {
		Controller qController = new Controller(args);
	}
	
	
	
}
