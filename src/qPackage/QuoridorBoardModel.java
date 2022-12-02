package qPackage;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
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
	
	// TODO Check if first and second valid indices
	// TODO Check if previously played
	public void completeMove(int first, int second, int playerNum)
	{
		Location currentLocation = getPlayerLocation(playerNum);
		Location otherPlayerLocation = getOtherPlayerLocation(playerNum);
		
		
		if (otherPlayerLocation.first == first && otherPlayerLocation.second == second)
		{
			throw new IllegalArgumentException("You can't play on top of the other player's location!!");
		}
		if (currentLocation.first == first && currentLocation.second == second)
		{
			throw new IllegalArgumentException("You can't play on top yourself!!");
		}
		
		if (first % 2 == 1 || second % 2 == 1)
		{
			gameBoard[first][second] = BLOCKED;
			
			if (shortestPathToWin(playerNum) == -1 || shortestPathToWin(getOtherPlayerNum(playerNum)) == -1)
			{
				gameBoard[first][second] = PASSABLE;
				throw new IllegalArgumentException("There wouldn't be a path to win then!!");
			}
			else
			{
				return;
			}
		}
		
		
		
		if (shortestPathToPlayer(first, second, playerNum) == 1)
		{
			setPlayerLocation(first, second, playerNum);
		}
		else
		{
			throw new IllegalArgumentException("You can't move that far!");
		}
		
		
		
	}
	
	
	
	public int shortestPathToWin(int playerNum)
	{
		int smallestCost = Integer.MAX_VALUE;
		
		
		for (int col = 0; col < gameBoard[0].length; col++)
		{
			int pathLength = shortestPathToPlayer(winningRow(playerNum), col, playerNum);
			
			if (pathLength == -1)
			{
				continue;
			}
			if (pathLength < smallestCost)
			{
				smallestCost = pathLength;
			}
		}
		
		
		if (smallestCost == Integer.MAX_VALUE)
		{
			return -1;
		}
		else
		{
			return smallestCost;
		}
		
		
		
	}
	
	
	
	public int winningRow(int playerNum)
	{
		if (playerNum == 1)
		{
			return gameBoard.length - 1;
		}
		if (playerNum == 2)
		{
			return 0;
		}
		else
		{
			throw new IllegalArgumentException("Doesn't have a winning row!");
		}
	}
	
	
	
	public int shortestPathToPlayer(int first, int second, int playerNum) {
		
		
		Location playerLocation = getPlayerLocation(playerNum);
		Location otherPlayerLocation = getOtherPlayerLocation(playerNum);
		
		// TODO Might be extra memory
		int[][] completionTable = new int[gameBoard.length][gameBoard.length];
		
		
		PriorityQueue<Location> pQueue= new PriorityQueue<Location>();
		
		
		pQueue.add(new Location(first, second, 0));
		
		
		while(pQueue.isEmpty() == false)
		{
		
			Location currentLocation = pQueue.remove();
			int addedCost = 1;
			completionTable[currentLocation.first][currentLocation.second] = DONE;
			
			if (currentLocation.first == playerLocation.first && currentLocation.second == playerLocation.second)
			{
				return currentLocation.cost;
			}
			
			if (currentLocation.first == otherPlayerLocation.first && currentLocation.second == otherPlayerLocation.second)
			{
				addedCost = 0;
			}
			
			
			// If not done, check if more steps possible in current path
			// Because there are never barriers around the edge of the board, if +/- 1 is valid, then +/- 2 will also have valid index
			
			
			// Checks left
			if (validBoardIndex(currentLocation.second - 1) && gameBoard[currentLocation.first][currentLocation.second - 1] == PASSABLE)
			{
				// If next not already dequeued
				if(gameBoard[currentLocation.first][currentLocation.second - 2] != DONE)
				{
					pQueue.add(new Location(currentLocation.first, currentLocation.second - 2, currentLocation.cost + addedCost));
				}
			}
			
			// Checks right
			if (validBoardIndex(currentLocation.second + 1) && gameBoard[currentLocation.first][currentLocation.second + 1] == PASSABLE)
			{
				// If next not already dequeued
				if(gameBoard[currentLocation.first][currentLocation.second + 2] != DONE)
				{
					pQueue.add(new Location(currentLocation.first, currentLocation.second + 2, currentLocation.cost + addedCost));
				}
			}
			
			// Checks up
			if (validBoardIndex(currentLocation.first - 1) && gameBoard[currentLocation.first - 1][currentLocation.second] == PASSABLE)
			{
				// If next not already dequeued
				if(gameBoard[currentLocation.first][currentLocation.first - 2] != DONE)
				{
					pQueue.add(new Location(currentLocation.first - 2, currentLocation.second, currentLocation.cost + addedCost));
				}
			}
			
			// Checks down
			if (validBoardIndex(currentLocation.first + 1) && gameBoard[currentLocation.first + 1][currentLocation.second] == PASSABLE)
			{
				// If next not already dequeued
				if(gameBoard[currentLocation.first][currentLocation.first + 2] != DONE)
				{
					pQueue.add(new Location(currentLocation.first + 2, currentLocation.second, currentLocation.cost + addedCost));
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
	
	
	public void setPlayerLocation(int first, int second, int playerNum)
	{
		return;
	}
	
	public Location getOtherPlayerLocation(int playerNum)
	{
		return null;
	}
	
	
	public int getOtherPlayerNum(int playerNum)
	{
		if (playerNum == 1)
		{
			return 2;
		}
		if (playerNum == 2)
		{
			return 1;
		}
		else
		{
			return -1;
		}
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
