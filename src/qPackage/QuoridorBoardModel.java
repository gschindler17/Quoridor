package qPackage;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.PriorityQueue;

public class QuoridorBoardModel {

	/** A helper object to handle observer pattern behavior */
	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	
	
	final int DONE = 1;
	
	final int PASSABLE = 0;
	final int BLOCKED = 1;
	
	
	
	private int boardSize;
	
	int[][] gameBoard; 
	
	public QuoridorBoardModel () {
		
		// Tells the view a model was instantiated
		pcs.firePropertyChange("instantiation", null, null);
		
		// Sets the gameBoard to a size 3 square
		setBoardSize(5);
		return;
		
	}
	
	
	
	
	
	
	
	
	public void setBoardSize(int _boardSize)
	{
		if (_boardSize > 0)
		{
			boardSize = _boardSize;
		}
		
		// If there are n spaces, then there are n-1 barriers too
		gameBoard = new int[(boardSize * 2) - 1][(boardSize * 2) - 1];
	}
	
	
	
	public int shortestPathToEnd(int playerNum) {
		
		
		Location playerLocation = getPlayerLocation(playerNum);
		
		// TODO Might be extra memory
		int[][] completionTable = new int[gameBoard.length][gameBoard.length];
		
		
		PriorityQueue<Location> pQueue= new PriorityQueue<Location>();
		
		
		pQueue.add(playerLocation);
		
		
		while(pQueue.isEmpty() == false)
		{
		
			Location currentLocation = pQueue.remove();
			completionTable[currentLocation.first][currentLocation.second] = DONE;
			
			//-------------------------
			// Short Circuit Conditions
			//-------------------------
			
			// Player 1 Conditions
			if(playerNum == 1)
			{
				if (currentLocation.first == gameBoard.length - 1)
				{
					return currentLocation.cost;
				}
			}
			// Player 2 Conditions
			if(playerNum == 2)
			{
				if (currentLocation.first == 0)
				{
					return currentLocation.cost;
				}
			}
			
			
			
			// If not done, check if more steps possible in current path
			// Because there are never barriers around the edge of the board, if +/- 1 is valid, then +/- 2 will also have valid index
			
			
			// Checks left
			if (validBoardIndex(currentLocation.second - 1) && gameBoard[currentLocation.first][currentLocation.second - 1] == PASSABLE)
			{
				// If next not already dequeued
				if(gameBoard[currentLocation.first][currentLocation.second - 2] != DONE)
				{
					pQueue.add(new Location(currentLocation.first, currentLocation.second - 2, currentLocation.cost + 1));
				}
			}
			
			// Checks right
			if (validBoardIndex(currentLocation.second + 1) && gameBoard[currentLocation.first][currentLocation.second + 1] == PASSABLE)
			{
				// If next not already dequeued
				if(gameBoard[currentLocation.first][currentLocation.second + 2] != DONE)
				{
					pQueue.add(new Location(currentLocation.first, currentLocation.second + 2, currentLocation.cost + 1));
				}
			}
			
			// Checks up
			if (validBoardIndex(currentLocation.first - 1) && gameBoard[currentLocation.first - 1][currentLocation.second] == PASSABLE)
			{
				// If next not already dequeued
				if(gameBoard[currentLocation.first][currentLocation.first - 2] != DONE)
				{
					pQueue.add(new Location(currentLocation.first - 2, currentLocation.second, currentLocation.cost + 1));
				}
			}
			
			// Checks down
			if (validBoardIndex(currentLocation.first + 1) && gameBoard[currentLocation.first + 1][currentLocation.second] == PASSABLE)
			{
				// If next not already dequeued
				if(gameBoard[currentLocation.first][currentLocation.first + 2] != DONE)
				{
					pQueue.add(new Location(currentLocation.first + 2, currentLocation.second, currentLocation.cost + 1));
				}
			}
			
			
		} // End While Loop
		
		// If the final destination was never reached, return -1
		return -1;
		
		
	}
	
	
	public boolean validBoardIndex(int val)
	{
		if (val >= 0)
		{
			if (val < gameBoard.length)
			{
				return true;
			}
		}
		return false;
	}
	
	
	public Location getPlayerLocation(int playerNum)
	{
		return null;
	}
	
	
	
	
	private class Location implements Comparable<Location> {
		
		public int first;
		
		public int second;
		
		public int cost;
		
		public Location(int _first, int _second, int _cost)
		{
			first = _first;
			second = _second;
			cost = _cost;
		}

		@Override
		public int compareTo(Location comp) {
			if (this.cost > comp.cost)
			{
				return 1;
			}
			else if (this.cost == comp.cost)
			{
				return 0;
			}
			else
			{
				return -1;
			}
		}
	}
	
	
	
	/**
	 * Don't forget to create a way for Observers to subscribe to this
	 * @param listener
	 */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    /**
     * And Observers probably want to be able to unsubscribe as well
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }
	
}
