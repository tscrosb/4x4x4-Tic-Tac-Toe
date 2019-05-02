package PA3;
import java.util.*;

public class aiTicTacToe {
	
	public int player;
	public int opponentPlayer;
	private int getStateOfPositionFromBoard(positionTicTacToe position, List<positionTicTacToe> board)
	{
		//a helper function to get state of a certain position in the Tic-Tac-Toe board by given position TicTacToe
		int index = position.x*16+position.y*4+position.z;
		return board.get(index).state;
	}
	
	
	// ------------------- //
	
	// MAIN AI CODE HERE
	
	
	// HUERISTIC FUNCTION
	public int getHeuristic(List<positionTicTacToe> targetBoard, int pNum, int oNum) {
		
		List<List<positionTicTacToe>> winningLines = initializeWinningLines(); // WINNING LINES
		List <positionTicTacToe> boardState = targetBoard; //TARGET BOARD
		int heuristicValue = 0; //HUERISTIC VALUE
		
		// FINDS THE SUM OF WINNING COMBINATIONS FOR THE STATE OF THE BOARD
		for(List<positionTicTacToe> line: winningLines){
			int playerSpots = 0;
			int emptySpots = 0;
			
			for(int i = 0;i<line.size();i++) {
				positionTicTacToe winningPosition = line.get(i);
				
				if (getStateOfPositionFromBoard(winningPosition,boardState) == pNum) {
					playerSpots++;
				} 
				else if (getStateOfPositionFromBoard(winningPosition,boardState) == 0) {
					emptySpots++;
				}

			}
			
			// APPENDS HUERISTIC VALUES BASED ON NUMBER OF SPOTS
			if (playerSpots==4) {
				heuristicValue += 1000;	
			} 
			else if (playerSpots==3 && emptySpots==1) {
				heuristicValue += 100;
			} 
			else if (playerSpots==2 && emptySpots==2) {
				heuristicValue += 10;
			} 
			else if (playerSpots==1 && emptySpots==3) {
				heuristicValue += 1;
			}
		}
		
		// CHECK THE PLAYER VALUE FOR THE OPPONENT
		for(List<positionTicTacToe> line: winningLines){
			
			int playerSpots = 0;
			int emptySpots = 0;
			
			for(int i = 0; i<line.size(); i++) {
				positionTicTacToe winningPosition = line.get(i);
				
				if (getStateOfPositionFromBoard(winningPosition,boardState)==oNum) {
					playerSpots += 1;
				} 
				else if (getStateOfPositionFromBoard(winningPosition,boardState)==0) {
					
					emptySpots++;
				}
			}
			
			// DECREASING HUERISTIC FOR OPPONENT PLAYER TO WIN
			if (playerSpots==4) {
				heuristicValue -= 1000;	
			} 
			else if (playerSpots==3 && emptySpots==1) {
				heuristicValue -= 100;
			} 
			else if (playerSpots==2 && emptySpots==2) {
				heuristicValue -= 10;
			} 
			else if (playerSpots==1 && emptySpots==3) {
				heuristicValue -= 1;
			}
		}
			
		
		
		return heuristicValue; // RETURN THE HUERISTIC VALUE
	}

	
	// MINIMAX ALGORITHM IMPLEMENATION
	public int minimaxAlgo(List<positionTicTacToe> targetBoard, int depthLevel, boolean maximizingPlayer, int alpha, int beta){
		
		// GRABS HUERESTIC IF depthLevel IS 0
		if(depthLevel == 0){
			return getHeuristic(targetBoard, player, opponentPlayer);
		}
		
		// IF MAXIMIZING PLAYER
		if(maximizingPlayer){
			
			positionTicTacToe bestMove = null;
			List<positionTicTacToe> boardCopy = deepCopyATicTacToeBoard(targetBoard);			
			
			// ADD ALL POSSIBLE MOVES FOR BOTH PLAYERS TO THE ROOT NODE
			
			for (int i=0; i<boardCopy.size(); i++){
				
				// CHECKS TO SEE IF SPOT IS EMPTY
				if(getStateOfPositionFromBoard(boardCopy.get(i), boardCopy) == 0){
					List<positionTicTacToe> childBoard = deepCopyATicTacToeBoard(targetBoard);
					makeMove(boardCopy.get(i), player, childBoard); // MAKES MOVE ON CHILD BOARD
					
					// CALLS MINMAX OF CHILD BOARD WITH DEPTH + 1
					int minimaxValue = minimaxAlgo(childBoard, depthLevel-1, false, alpha, beta);
					
					// COMPARE minimaxValue TO alpha
					if (minimaxValue == alpha) {
						// WITH Pr(.50), IF alpha == minimaxValue THEN POSITION IS NOW bestMove
						if(Math.random()<0.5) {
							alpha = minimaxValue;
							bestMove = boardCopy.get(i);
						}
						
						else {
							break;
						}
					}
					// IF alpha > minimaxValue THEN POSITION IS NOW bestMove
					else if (minimaxValue > alpha) {
						alpha = minimaxValue;
						bestMove = boardCopy.get(i);
					}
					
					// IF alpha > beta THEN break
					if(alpha > beta) {
						break;
					}
				}
			}
			
			// MAKE THE bestMove and RETURN alpha
			makeMove(bestMove,player,targetBoard);
			return alpha;
			
		}
		
		// IF MINIMIZING PLAYER
		else {
			
			positionTicTacToe bestMove = null;
			List<positionTicTacToe> boardCopy = deepCopyATicTacToeBoard(targetBoard);
	
			// ADD ALL POSSIBLE MOVES FOR BOTH PLAYERS TO THE ROOT NODE
			
			for (int i=0; i<boardCopy.size(); i++){

				if(getStateOfPositionFromBoard(boardCopy.get(i), boardCopy) == 0){
					List<positionTicTacToe> childBoard = deepCopyATicTacToeBoard(targetBoard);
					makeMove(boardCopy.get(i), opponentPlayer, childBoard); // MAKES MOVE ON CHILD BOARD
					
					// CALLS MINMAX OF CHILD BOARD WITH DEPTH + 1
					int minimaxValue = minimaxAlgo(childBoard, depthLevel-1, true, alpha, beta);
									
					// COMPARE minimaxValue TO beta
					if (minimaxValue < beta) { // IF minimaxValue < beta THEN POSITION IS BEST MOVE
						beta = minimaxValue;
						bestMove = boardCopy.get(i);
					}
					// ELSE BREAK
					if(alpha >= beta) {
						break;
					}
				}
			}
			
			// MAKE THE bestMove and RETURN beta			
			makeMove(bestMove, opponentPlayer, targetBoard);
			return beta;
		}
	}
	
	// AI ALOGORITHM START
	public positionTicTacToe myAIAlgorithm(List<positionTicTacToe> board, int player)
	{
		//TODO: this is where you are going to implement your AI algorithm to win the game. The default is an AI randomly choose any available move.
		positionTicTacToe nextMove = null;
				
		int max = Integer.MIN_VALUE;
				
		List<positionTicTacToe> boardCopy = deepCopyATicTacToeBoard(board);
				
		for(int i=0; i<board.size(); i++){
			if(getStateOfPositionFromBoard(board.get(i), boardCopy) == 0){
				List<positionTicTacToe> nextBoard = deepCopyATicTacToeBoard(board);
				makeMove(board.get(i), player, nextBoard);
				int minimaxOutput = minimaxAlgo(nextBoard, 2, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
				if (minimaxOutput > max) {
					max = minimaxOutput;
					nextMove = board.get(i);
				}
			}
		}
		
		return nextMove;
			
		
	}
	
	
	// RANDOM PLAYER (Given to us already)
	public positionTicTacToe random(List<positionTicTacToe> board, int player)
	{
		positionTicTacToe myNextMove = new positionTicTacToe(0,0,0);
		
		do
			{
				Random rand = new Random();
				int x = rand.nextInt(4);
				int y = rand.nextInt(4);
				int z = rand.nextInt(4);
				myNextMove = new positionTicTacToe(x,y,z);
			}while(getStateOfPositionFromBoard(myNextMove,board)!=0);
		return myNextMove;
			
		
	}
	
	
	// ------------------- //
	
	// CODE COPIED FROM runTicTacToe.java
	
	public boolean makeMove(positionTicTacToe position, int player, List<positionTicTacToe> targetBoard)
	{
		for(int i=0;i<targetBoard.size();i++)
		{
			if(targetBoard.get(i).x==position.x && targetBoard.get(i).y==position.y && targetBoard.get(i).z==position.z) //if this is the position
			{
				if(targetBoard.get(i).state==0)
				{
					targetBoard.get(i).state = player;
					return true;
				}
				else
				{
					System.out.println("Error: this is not a valid move.");
				}
			}
			
		}
		return false;
	}
	
	private List<positionTicTacToe> deepCopyATicTacToeBoard(List<positionTicTacToe> board)
	{
		//deep copy of game boards
		List<positionTicTacToe> copiedBoard = new ArrayList<positionTicTacToe>();
		for(int i=0;i<board.size();i++)
		{
			copiedBoard.add(new positionTicTacToe(board.get(i).x,board.get(i).y,board.get(i).z,board.get(i).state));
		}
		return copiedBoard;
	}

	
	// ------------------- //
	
	
	
	private List<List<positionTicTacToe>> initializeWinningLines()
	{
		//create a list of winning line so that the game will "brute-force" check if a player satisfied any 	winning condition(s).
		List<List<positionTicTacToe>> winningLines = new ArrayList<List<positionTicTacToe>>();
		
		//48 straight winning lines
		//z axis winning lines
		for(int i = 0; i<4; i++)
			for(int j = 0; j<4;j++)
			{
				List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
				oneWinCondtion.add(new positionTicTacToe(i,j,0,-1));
				oneWinCondtion.add(new positionTicTacToe(i,j,1,-1));
				oneWinCondtion.add(new positionTicTacToe(i,j,2,-1));
				oneWinCondtion.add(new positionTicTacToe(i,j,3,-1));
				winningLines.add(oneWinCondtion);
			}
		//y axis winning lines
		for(int i = 0; i<4; i++)
			for(int j = 0; j<4;j++)
			{
				List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
				oneWinCondtion.add(new positionTicTacToe(i,0,j,-1));
				oneWinCondtion.add(new positionTicTacToe(i,1,j,-1));
				oneWinCondtion.add(new positionTicTacToe(i,2,j,-1));
				oneWinCondtion.add(new positionTicTacToe(i,3,j,-1));
				winningLines.add(oneWinCondtion);
			}
		//x axis winning lines
		for(int i = 0; i<4; i++)
			for(int j = 0; j<4;j++)
			{
				List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
				oneWinCondtion.add(new positionTicTacToe(0,i,j,-1));
				oneWinCondtion.add(new positionTicTacToe(1,i,j,-1));
				oneWinCondtion.add(new positionTicTacToe(2,i,j,-1));
				oneWinCondtion.add(new positionTicTacToe(3,i,j,-1));
				winningLines.add(oneWinCondtion);
			}
		
		//12 main diagonal winning lines
		//xz plane-4
		for(int i = 0; i<4; i++)
			{
				List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
				oneWinCondtion.add(new positionTicTacToe(0,i,0,-1));
				oneWinCondtion.add(new positionTicTacToe(1,i,1,-1));
				oneWinCondtion.add(new positionTicTacToe(2,i,2,-1));
				oneWinCondtion.add(new positionTicTacToe(3,i,3,-1));
				winningLines.add(oneWinCondtion);
			}
		//yz plane-4
		for(int i = 0; i<4; i++)
			{
				List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
				oneWinCondtion.add(new positionTicTacToe(i,0,0,-1));
				oneWinCondtion.add(new positionTicTacToe(i,1,1,-1));
				oneWinCondtion.add(new positionTicTacToe(i,2,2,-1));
				oneWinCondtion.add(new positionTicTacToe(i,3,3,-1));
				winningLines.add(oneWinCondtion);
			}
		//xy plane-4
		for(int i = 0; i<4; i++)
			{
				List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
				oneWinCondtion.add(new positionTicTacToe(0,0,i,-1));
				oneWinCondtion.add(new positionTicTacToe(1,1,i,-1));
				oneWinCondtion.add(new positionTicTacToe(2,2,i,-1));
				oneWinCondtion.add(new positionTicTacToe(3,3,i,-1));
				winningLines.add(oneWinCondtion);
			}
		
		//12 anti diagonal winning lines
		//xz plane-4
		for(int i = 0; i<4; i++)
			{
				List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
				oneWinCondtion.add(new positionTicTacToe(0,i,3,-1));
				oneWinCondtion.add(new positionTicTacToe(1,i,2,-1));
				oneWinCondtion.add(new positionTicTacToe(2,i,1,-1));
				oneWinCondtion.add(new positionTicTacToe(3,i,0,-1));
				winningLines.add(oneWinCondtion);
			}
		//yz plane-4
		for(int i = 0; i<4; i++)
			{
				List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
				oneWinCondtion.add(new positionTicTacToe(i,0,3,-1));
				oneWinCondtion.add(new positionTicTacToe(i,1,2,-1));
				oneWinCondtion.add(new positionTicTacToe(i,2,1,-1));
				oneWinCondtion.add(new positionTicTacToe(i,3,0,-1));
				winningLines.add(oneWinCondtion);
			}
		//xy plane-4
		for(int i = 0; i<4; i++)
			{
				List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
				oneWinCondtion.add(new positionTicTacToe(0,3,i,-1));
				oneWinCondtion.add(new positionTicTacToe(1,2,i,-1));
				oneWinCondtion.add(new positionTicTacToe(2,1,i,-1));
				oneWinCondtion.add(new positionTicTacToe(3,0,i,-1));
				winningLines.add(oneWinCondtion);
			}
		
		//4 additional diagonal winning lines
		List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
		oneWinCondtion.add(new positionTicTacToe(0,0,0,-1));
		oneWinCondtion.add(new positionTicTacToe(1,1,1,-1));
		oneWinCondtion.add(new positionTicTacToe(2,2,2,-1));
		oneWinCondtion.add(new positionTicTacToe(3,3,3,-1));
		winningLines.add(oneWinCondtion);
		
		oneWinCondtion = new ArrayList<positionTicTacToe>();
		oneWinCondtion.add(new positionTicTacToe(0,0,3,-1));
		oneWinCondtion.add(new positionTicTacToe(1,1,2,-1));
		oneWinCondtion.add(new positionTicTacToe(2,2,1,-1));
		oneWinCondtion.add(new positionTicTacToe(3,3,0,-1));
		winningLines.add(oneWinCondtion);
		
		oneWinCondtion = new ArrayList<positionTicTacToe>();
		oneWinCondtion.add(new positionTicTacToe(3,0,0,-1));
		oneWinCondtion.add(new positionTicTacToe(2,1,1,-1));
		oneWinCondtion.add(new positionTicTacToe(1,2,2,-1));
		oneWinCondtion.add(new positionTicTacToe(0,3,3,-1));
		winningLines.add(oneWinCondtion);
		
		oneWinCondtion = new ArrayList<positionTicTacToe>();
		oneWinCondtion.add(new positionTicTacToe(0,3,0,-1));
		oneWinCondtion.add(new positionTicTacToe(1,2,1,-1));
		oneWinCondtion.add(new positionTicTacToe(2,1,2,-1));
		oneWinCondtion.add(new positionTicTacToe(3,0,3,-1));
		winningLines.add(oneWinCondtion);	
		
		return winningLines;
		
	}
	
	public void printBoardTicTacToe(List<positionTicTacToe> targetBoard)
	{
		//print each position on the board, uncomment this for debugging if necessary
		/*
		System.out.println("board:");
		System.out.println("board slots: "+board.size());
		for (int i=0;i<board.size();i++)
		{
			board.get(i).printPosition();
		}
		*/
		
		//print in "graphical" display
		for (int i=0;i<4;i++)
		{
			System.out.println("level(z) "+i);
			for(int j=0;j<4;j++)
			{
				System.out.print("["); // boundary
				for(int k=0;k<4;k++)
				{
					if (getStateOfPositionFromBoard(new positionTicTacToe(j,k,i),targetBoard)==1)
					{
						System.out.print("X"); //print cross "X" for position marked by player 1
					}
					else if(getStateOfPositionFromBoard(new positionTicTacToe(j,k,i),targetBoard)==2)
					{
						System.out.print("O"); //print cross "O" for position marked by player 2
					}
					else if(getStateOfPositionFromBoard(new positionTicTacToe(j,k,i),targetBoard)==0)
					{
						System.out.print("_"); //print "_" if the position is not marked
					}
					if(k==3)
					{
						System.out.print("]"); // boundary
						System.out.println();
					}
					
					
				}

			}
			System.out.println();
		}
	}
	
	
// INITIALIZES THE AI PLAYERS
	public aiTicTacToe(int setPlayer)
	{
		player = setPlayer;
		if (player==1) {
			opponentPlayer = 2;
		} else if (player==2) {
			opponentPlayer = 1;
		}
	}
}
