package qPackage;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.PriorityQueue;

public class QuoridorBoardModel {

	/** A helper object to handle observer pattern behavior */
	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	final int OCCUPIED1 = 1;
	final int OCCUPIED2 = 2;
	final int UNOCCUPIED = 0;
	
	final int DONE = 1;
	
	final int PASSABLE = 0;
	final int BLOCKED = 1;
	
	
	private int currentPlayer;
	
	private Location player1Location;
	
	private Location player2Location;
	
	
	private int boardSize;
	
	int[][] gameBoard; 
	
	public QuoridorBoardModel () {
		
		currentPlayer = 1;
		
		player1Location = new Location(0, 0, 0);
		player2Location = new Location(0, 0, 0);
		
		
		
		// Tells the view a model was instantiated
		pcs.firePropertyChange("instantiation", null, null);
		
		
		
		// Sets the gameBoard to a size
		boardSize = 5;
		
		setBoardSize(5);
		
		
	}
	
	
	
	
	
	
	public void setBoardSize(int _boardSize)
	{
		if (_boardSize % 2 == 0)
		{
			throw new IllegalArgumentException("You can't have a board that doesn't have a middle column!!");
		}
		
		
		
		if (_boardSize > 0)
		{
			boardSize = _boardSize;
		}
	
		// If there are n spaces, then there are n-1 barriers too
		gameBoard = new int[(boardSize * 2) - 1][(boardSize * 2) - 1];
		
		
		int middleCol = getMiddleCol();
		
		
		setPlayerLocation(0, middleCol, 1);
		setPlayerLocation((boardSize * 2) - 2, middleCol, 2);
		
		
		pcs.firePropertyChange("setSize", null, boardSize);
	}
	
	public int getMiddleCol()
	{
		return ((boardSize * 2) - 1) / 2;
	}
	
	
	
	
	
	
	
	// TODO Check if first and second valid indices
	// TODO Check if previously played
	public void completeMove(int first, int second)
	{
		
		int playerNum = currentPlayer;
		
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
		
		if (isBarrier(first, second) && !(isNullBarrier(first, second)))
		{
			gameBoard[first][second] = BLOCKED;
			
			if (shortestPathToWin(playerNum) == -1 || shortestPathToWin(getOtherPlayerNum(playerNum)) == -1)
			{
				gameBoard[first][second] = PASSABLE;
				throw new IllegalArgumentException("There wouldn't be a path to win then!!");
			}
			else
			{
				nextTurn();
				return;
			}
		}
		
		
		System.out.println("\nYou're shortest path was: " + shortestPathToPlayer(first, second, playerNum));
		if (shortestPathToPlayer(first, second, playerNum) == 1)
		{
			setPlayerLocation(first, second, playerNum);
			nextTurn();
		}
		else
		{
			throw new IllegalArgumentException("You can't move that far!");
		}
		
		
		
	}
	
	
	
	public int shortestPathToWin(int playerNum)
	{
		int smallestCost = Integer.MAX_VALUE;
		
		
		for (int col = 0; col < gameBoard[0].length; col = col + 2)
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
		
		int[][] completionTable = new int[gameBoard.length][gameBoard.length];
		
		
		PriorityQueue<Location> pQueue= new PriorityQueue<Location>();
		
		
		pQueue.add(new Location(first, second, 0));
		
		
		while(!(pQueue.isEmpty()))
		{
			
			Location currentLocation = pQueue.remove();
			
			System.out.println("\nCurrent Location:" + "\nfirst: " + currentLocation.first + "\nsecond: " + currentLocation.second + "\ncost: " + currentLocation.cost);
			
			
			
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
				if(completionTable[currentLocation.first][currentLocation.second - 2] != DONE)
				{
					pQueue.add(new Location(currentLocation.first, currentLocation.second - 2, currentLocation.cost + addedCost));
				}
			}
			
			// Checks right
			if (validBoardIndex(currentLocation.second + 1) && gameBoard[currentLocation.first][currentLocation.second + 1] == PASSABLE)
			{
				// If next not already dequeued
				if(completionTable[currentLocation.first][currentLocation.second + 2] != DONE)
				{
					pQueue.add(new Location(currentLocation.first, currentLocation.second + 2, currentLocation.cost + addedCost));
				}
			}
			
			// Checks up
			if (validBoardIndex(currentLocation.first - 1) && gameBoard[currentLocation.first - 1][currentLocation.second] == PASSABLE)
			{
				// If next not already dequeued
				if(completionTable[currentLocation.first - 2][currentLocation.second] != DONE)
				{
					pQueue.add(new Location(currentLocation.first - 2, currentLocation.second, currentLocation.cost + addedCost));
				}
			}
			
			// Checks down
			if (validBoardIndex(currentLocation.first + 1) && gameBoard[currentLocation.first + 1][currentLocation.second] == PASSABLE)
			{
				// If next not already dequeued
				if(completionTable[currentLocation.first + 2][currentLocation.second] != DONE)
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
		if (playerNum == 1)
		{
			return player1Location;
		}
		if (playerNum == 2)
		{
			return player2Location;
		}
		else
		{
			throw new IllegalArgumentException("Can't get that player's location as they don't exist!!");
		}
	}
	
	// TODO Change methods to check if valid player location
	private void setPlayerLocation(int first, int second, int playerNum)
	{
		Location playerLocation = getPlayerLocation(playerNum);
		
		if (validBoardIndex(first) && validBoardIndex(second))
		{
			
			if (getPlayerLocation(playerNum) == null && playerNum == 1)
			{
				player1Location = new Location(0,0,0);
			}
			if (getPlayerLocation(playerNum) == null && playerNum == 2)
			{
				player2Location = new Location(0,0,0);
			}
			
			
			if (validBoardIndex(playerLocation.first) && validBoardIndex(playerLocation.second))
			{
				gameBoard[playerLocation.first][playerLocation.second] = UNOCCUPIED;
			}
			
			playerLocation.first = first;
			playerLocation.second = second;
			
			if (playerNum == 1)
			{
				gameBoard[playerLocation.first][playerLocation.second] = OCCUPIED1;
			}
			else
			{
				gameBoard[playerLocation.first][playerLocation.second] = OCCUPIED2;
			}
		}
		else
		{
			throw new IllegalArgumentException("You can't move a player to an invalid location!!");
		}
	}
	
	public Location getOtherPlayerLocation(int playerNum)
	{
		if (playerNum == 1)
		{
			return player2Location;
		}
		else if (playerNum == 2)
		{
			return player1Location;
		}
		else
		{
			throw new IllegalArgumentException("You can't find another player because you have an invalid playerNum!!");
		}
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
	
	
	public int[][] getGameBoard()
	{
		return gameBoard;
	}
	
	public boolean isNullBarrier(int first, int second)
	{
		if (first % 2 == 1 && second % 2 == 1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean isHorizontalBarrier(int first, int second)
	{
		if (first % 2 == 1 && second % 2 == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	public boolean isVerticalBarrier(int first, int second) 
	{
		if (second % 2 == 1 && first % 2 == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	public boolean isPlayerSquare(int first, int second)
	{
		if (first % 2 == 0 && second % 2 == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean isBarrier(int first, int second)
	{
		if (first % 2 == 1 || second % 2 == 1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean passable(int first, int second)
	{
		if (gameBoard[first][second] == PASSABLE)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean occupiedPlayer1(int first, int second)
	{
		if (gameBoard[first][second] == OCCUPIED1)
		{
			return true;
		}
		else
		{
			return false;
		}
	} 
	
	public boolean occupiedPlayer2(int first, int second)
	{
		if (gameBoard[first][second] == OCCUPIED2)
		{
			return true;
		}
		else
		{
			return false;
		}
	} 
	
	public void nextTurn() {
		if (currentPlayer == 1)
		{
			currentPlayer = 2;
		}
		else if(currentPlayer == 2)
		{
			currentPlayer = 1;
		}
		
		pcs.firePropertyChange("nextTurn", null, currentPlayer);
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
