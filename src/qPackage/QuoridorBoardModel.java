package qPackage;

import java.util.PriorityQueue;

public class QuoridorBoardModel {

	final int COST = 0;
	final int FROM = 1;
	final int DONE = 2;
	
	
	final int PASSABLE = 0;
	final int BLOCKED = 1;
	
	
	
	int boardSize;
	
	int[][] gameBoard; 
	
	
	
	
	
	public int shortestPathToEnd(int playerNum) {
		
		
		Location playerLocation = getPlayerLocation(playerNum);
		
		
		int[][][] graph = new int[this.boardSize][this.boardSize][3];
		PriorityQueue<Location> pQueue= new PriorityQueue<Location>();
		
		
		pQueue.add(playerLocation);
		
		
		while(pQueue.isEmpty() == false)
		{
		
			Location currentLocation = pQueue.remove();
			
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
				pQueue.add(new Location(currentLocation.first, currentLocation.second - 2, currentLocation.cost + 1));
			}
			
			// Checks right
			if (validBoardIndex(currentLocation.second + 1) && gameBoard[currentLocation.first][currentLocation.second + 1] == PASSABLE)
			{
				pQueue.add(new Location(currentLocation.first, currentLocation.second + 2, currentLocation.cost + 1));
			}
			
			// Checks up
			if (validBoardIndex(currentLocation.first - 1) && gameBoard[currentLocation.first - 1][currentLocation.second] == PASSABLE)
			{
				pQueue.add(new Location(currentLocation.first - 2, currentLocation.second, currentLocation.cost + 1));
			}
			
			// Checks down
			if (validBoardIndex(currentLocation.first + 1) && gameBoard[currentLocation.first + 1][currentLocation.second] == PASSABLE)
			{
				pQueue.add(new Location(currentLocation.first + 2, currentLocation.second, currentLocation.cost + 1));
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
	
	
}