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
	
	
	/**
	 * Constructor
	 * Instantiates the "brains" of the game
	 */
	public QuoridorBoardModel () {
		
		// Player 1 starts first
		currentPlayer = 1;
		
		// Instantiates the players' locations
		player1Location = new Location(0, 0, 0);
		player2Location = new Location(0, 0, 0);
		
		
		
		// Tells the view a model was instantiated
		pcs.firePropertyChange("instantiation", null, null);
		
		
		
		// Sets the gameBoard to a size
		boardSize = 5;
		
		setBoardSize(boardSize);
		
		
	}
	
	
	
	
	
	/**
	 * Sets the board to a specified sizes
	 * @param _boardSize
	 * @throws IllegalArgumentException if the board is even or too small
	 */
	public void setBoardSize(int _boardSize)
	{
		// Handling illegal input
		if (_boardSize % 2 == 0)
		{
			throw new IllegalArgumentException("You can't have a board that doesn't have a middle column!!");
		}
		if (_boardSize < 3)
		{
			throw new IllegalArgumentException("You can't have a board that small!");
		}
		
		
		if (_boardSize >= 3)
		{
			boardSize = _boardSize;
		}
	
		// If there are n spaces, then there are n-1 barriers too
		gameBoard = new int[(boardSize * 2) - 1][(boardSize * 2) - 1];
		
		
		
		int middleCol = getMiddleCol();
		
		// Starts the players off in the middle of their respective row
		setPlayerLocation((boardSize * 2) - 2, middleCol, 2);
		setPlayerLocation(0, middleCol, 1);
		
		
		// Tells the model that the size has been set
		pcs.firePropertyChange("setSize", null, boardSize);
	}
	
	/**
	 * Sets the board to its original state at the current size
	 */
	public void resetBoard()
	{	
		setBoardSize(boardSize);	
	}
	
	// Returns the middle col that the players should default start at
	public int getMiddleCol()
	{
		return ((boardSize * 2) - 1) / 2;
	}
	
	
	
	/**
	 * Takes in the coordinates that the currentPlayer wants to move to
	 * 
	 * @param first
	 * @param second
	 */
	public void completeMove(int first, int second)
	{
		
		int playerNum = currentPlayer;
		
		Location currentLocation = getPlayerLocation(playerNum);
		Location otherPlayerLocation = getOtherPlayerLocation(playerNum);
		
		
		// Ensuring the input is valid
		if (!(validBoardIndex(first)) || !(validBoardIndex(second)))
		{
			throw new IllegalArgumentException("You can't move to an invalid space!!");
		}
		if (otherPlayerLocation.first == first && otherPlayerLocation.second == second)
		{
			throw new IllegalArgumentException("You can't play on top of the other player's location!!");
		}
		if (currentLocation.first == first && currentLocation.second == second)
		{
			throw new IllegalArgumentException("You can't play on top yourself!!");
		}
		if (isNullBarrier(first, second))
		{
			throw new IllegalArgumentException("You can't play in crux of all of the barriers!!");
		}
		
		
		// If the location that the player plays is an actual barrier
		if (isBarrier(first, second))
		{
			if(gameBoard[first][second] == BLOCKED)
			{
				throw new IllegalArgumentException("Someone already filled that barrier!!");
			}
			
			// Set it to blocked
			gameBoard[first][second] = BLOCKED;
			
			// If this blocks a winning path for either player, state that the barrier cannot be played
			if (shortestPathToWin(playerNum) == -1 || shortestPathToWin(getOtherPlayerNum(playerNum)) == -1)
			{
				gameBoard[first][second] = PASSABLE;
				throw new IllegalArgumentException("There wouldn't be a path to win then!!");
			}
			else
			{
				// Switch to the next turn
				nextTurn();
				return;
			}
		}
		
		// If it is not a barrier, then it is a player space
		// If it is within a reach of 1, then it is a valid player space
		if (shortestPathToPlayer(first, second, playerNum) == 1)
		{
			setPlayerLocation(first, second, playerNum);
			
			// Check to see if the current player has won with this move
			if (hasWon(playerNum))
			{
				pcs.firePropertyChange("winner", null, currentPlayer);
				return;
			}
			// else, go to the next turn
			else
			{
				nextTurn();
			}
		}
		// if > 1, then it isn't a valid space to move to in one turn and requires more than 1 turn to complete
		else
		{
			throw new IllegalArgumentException("You can't move that far!");
		}
		
		
		
	}
	
	
	/**
	 * Checks to see if the playerNum has won
	 * @param playerNum
	 * @return true if the player has completed the game
	 */
	public boolean hasWon(int playerNum) {
		Location currentPlayerLocation = getPlayerLocation(playerNum);
		
		if(currentPlayerLocation.first == winningRow(playerNum))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	
	/**
	 * 
	 * @param playerNum
	 * @return
	 */
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
	
	
	/**
	 * Finds the winning row based off of what playerNum given
	 * @param playerNum
	 * @return
	 */
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
	
	
	// KEY METHOD WITHIN THE QUORIDORBOARDMODEL
	/**
	 * Checks to see what the shortest path is from a certain place to the playerNum given
	 * @param first
	 * @param second
	 * @param playerNum
	 * @return integer of the shortest path between the player and said space
	 */
	public int shortestPathToPlayer(int first, int second, int playerNum) {
		
		
		Location playerLocation = getPlayerLocation(playerNum);
		Location otherPlayerLocation = getOtherPlayerLocation(playerNum);
		
		int[][] completionTable = new int[gameBoard.length][gameBoard.length];
		
		
		PriorityQueue<Location> pQueue= new PriorityQueue<Location>();
		
		// Adds the current node with a cost of 0 to the PriorityQueue
		pQueue.add(new Location(first, second, 0));
		
		// Until the queue is empty
		while(!(pQueue.isEmpty()))
		{
			
			Location currentLocation = pQueue.remove();
			
			
			int addedCost = 1;
			completionTable[currentLocation.first][currentLocation.second] = DONE;
			
			// If the dequeued item is the player, then return the cost (which is the lowest cost to the player)
			if (currentLocation.first == playerLocation.first && currentLocation.second == playerLocation.second)
			{
				return currentLocation.cost;
			}
			// If landing on the other player, don't increase the cost for adjacent
			// Hopping feature
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
	
	/**
	 * Ensures that the integer given is within the board
	 * @param val
	 * @return
	 */
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
	
	/**
	 * Moves the player to the location given
	 * @param first
	 * @param second
	 * @param playerNum
	 */
	private void setPlayerLocation(int first, int second, int playerNum)
	{
		Location playerLocation = getPlayerLocation(playerNum);
		
		if (validBoardIndex(first) && validBoardIndex(second))
		{
			
			// If the player doesn't have a Location object yet, create a blank one
			if (getPlayerLocation(playerNum) == null && playerNum == 1)
			{
				player1Location = new Location(0,0,0);
			}
			if (getPlayerLocation(playerNum) == null && playerNum == 2)
			{
				player2Location = new Location(0,0,0);
			}
			
			// Ensure that the player's previous location is UNOCCUPIED
			if (validBoardIndex(playerLocation.first) && validBoardIndex(playerLocation.second))
			{
				gameBoard[playerLocation.first][playerLocation.second] = UNOCCUPIED;
			}
			
			playerLocation.first = first;
			playerLocation.second = second;
			
			// Move the player to the new location
			if (playerNum == 1)
			{
				gameBoard[playerLocation.first][playerLocation.second] = OCCUPIED1;
			}
			else if (playerNum == 2)
			{
				gameBoard[playerLocation.first][playerLocation.second] = OCCUPIED2;
			}
		}
		else
		{
			throw new IllegalArgumentException("You can't move a player to an invalid location!!");
		}
	}
	
	/**
	 * If 1, get 2
	 * If 2, get 1
	 * @param playerNum
	 * @return
	 */
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
	
	public int getCurrentPlayer() {
		return currentPlayer;
	}
	
	
	
	/**
	 * Internal class that stores indices for a 2D array
	 * and stores the cost
	 * 
	 */
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
