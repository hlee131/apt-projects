import java.util.Scanner;
import java.util.Arrays;

// represents a single game of tic tac toe 
public class Game {

	// board size length and width
	private final int boardSize; 

	// cross and circle string constants with ansi color codes
	private static final String CROSS = "\u001B[34mX\u001B[0m"; 
	private static final String CIRCLE = "\u001B[31mO\u001B[0m";  

	// possible square values
	// p1 will be CIRCLE, p2 will be CROSS
	public enum States {
		EMPTY, CROSS, CIRCLE
	};

	// possible win axises 
	private enum Axis {
		ROW, COLUMN, LR_DIAGONAL, RL_DIAGONAL 
	}

	// board representation
	public States[][] board;

	// current player (can be 0 or 1)
	private int currentPlayer;

	// usernames 
	private String[] players;

    // bot object
    private Bot b; 

    public int getBoardSize() { return this.boardSize; }

	/*
	constructor to create a new game 

	arguments:
		p1 - player 1 username 
		p2 - player 2 username 
		boardSize - length and width of board 

	preconditions:
		boardSize is > 1
	*/
	public Game(String p1, String p2, int boardSize, boolean useBot) {
		// initialize game state
		this.boardSize = boardSize;  
		this.board = new States[boardSize][boardSize];

		for (int i = 0; i < boardSize; i++) {
			Arrays.fill(board[i], States.EMPTY); 
		}
		this.currentPlayer = 0;
		this.players = new String[2];
		this.players[0] = p1;
		this.players[1] = p2;
        if (useBot) this.b = new Bot(this);
	}

	/*
	the run method runs one game of tic tac toe 

	return values:
		0.5 - tie game 
		-1.0 - player 1 wins 
		1.0 - player 2 wins 
	*/
	public double run() {
		Scanner in = new Scanner(System.in);

		// game loop 
		while (true) {
            int[] rowCol; 

            // if it is a single player game, ask bot for its move 
            if (b != null && currentPlayer == 1) {
                int move = b.nextMove(); 
                System.out.println(move);
                rowCol = getIndexes(move);
            } else {
                // ask player which square they want to select 
                printBoard();
                System.out.printf("%s, which square? ", players[currentPlayer]);
                int squareNum = Helper.nextInt(in);
                
                // check if selection is within range 
                while (squareNum < 1 || squareNum > (boardSize * boardSize)) {
                    System.out.printf("Number has to be 1-%d: ", boardSize * boardSize); 
                    squareNum = Helper.nextInt(in); 
                }

                // generate indexes for the square that was selected 
                rowCol = getIndexes(squareNum);

                // confirm the square is an empty square
                while (this.board[rowCol[0]][rowCol[1]] != States.EMPTY) {
                    System.out.print("Square has to be empty: "); 
                    squareNum = Helper.nextInt(in); 

                    // check selection is within range 
                    while (squareNum < 1 || squareNum > (boardSize * boardSize)) {
                        System.out.printf("Number has to be 1-%d: ", boardSize * boardSize); 
                        squareNum = Helper.nextInt(in); 
                    }

                    rowCol = getIndexes(squareNum); 
                }
            }

			// update board with input 
			this.board[rowCol[0]][rowCol[1]] = this.currentPlayer == 1 ? States.CROSS : States.CIRCLE;

			// flip the current player with xor 
			this.currentPlayer ^= 1; 

			// check for tie and win conditions 
			double winner = findWinner();
			if (winner != 0.0) {
				printBoard();
				System.out.printf("%s won the game!\n", winner < 0 ? players[0] : players[1]); 
				return winner; 
			} else if (boardFull()) {
				printBoard(); 
				System.out.println("It's a tie!");
				return 0.5;
			}
		}
	}

	/*
	the boardFull method checks if the board is full 

	return values:
		true - board is full
		false - board is not full 
	*/
	public boolean boardFull() {
		// return false once an empty square is found 
		for (int i = 0; i < boardSize; i++) {
			for (int ii = 0; ii < boardSize; ii++) {
				if (this.board[i][ii] == States.EMPTY) return false;  
			}
		}

		return true; 
	}

	/*
	the findWinner method looks for a consecutive, winning sequence 

	return values:
		0.0 - no winning sequence 
		-1.0 - player 1 wins 
		1.0 - player 2 wins 
	*/
	public double findWinner() {
		// check each column 
		for (int i = 0; i < boardSize; i++) {
			double win = checkIdentical(0, i, Axis.COLUMN); 
			if (win != 0.0) return win; 
		}

		// check each row 
		for (int i = 0; i < boardSize; i++) {
			double win = checkIdentical(i, 0, Axis.ROW); 
			if (win != 0.0) return win; 
		}

		// check each diagonal
		double winLR = checkIdentical(0, 0, Axis.LR_DIAGONAL);
		double winRL = checkIdentical(0, boardSize - 1, Axis.RL_DIAGONAL); 
		if (winLR != 0.0) return winLR;
		else if (winRL != 0.0) return winRL; 

		return 0.0; 
	}

	/*
	the checkIdentical method looks for a sequence of identical values 

	arguments: 
		startRow - first index of the square to start searching at
		startCol - second index of the square to start searching at 
		axis - the direction to search in 

	preconditions: 
		startRow and startCol are valid indexes 

	return values: 
		0.0 - no winning sequence 
		-1.0 - player 1 wins 
		1.0 - player 2 wins 
	*/
	private double checkIdentical(int startRow, int startCol, Axis axis) {
		States prev = this.board[startRow][startCol]; 

		// update locations 
		// for all axises except row, always update to next row 
		startRow += axis != Axis.ROW ? 1 : 0; 
		// for all axises except column, always update the column 
		// only the right-left diagonal moves to the left column (-1), everything else
		// moves right (+1); 
		startCol += axis != Axis.COLUMN ? (axis == Axis.RL_DIAGONAL ? -1 : 1) : 0; 

		States next = this.board[startRow][startCol];

		while (true) {
			// check if previous and current, if not, the chain has been broken 
			// make sure the code isn't seeing a chain of empty squares as a win 
			if (prev == next && prev != States.EMPTY) {
				// checks if we reached the bounds of the board 
				if ((startRow == boardSize - 1 && axis != Axis.ROW) ||
					(startCol == boardSize - 1 && axis == Axis.ROW)) return next == States.CIRCLE ? -1.0 : 1.0;
				else {
					// update previous to current 
					prev = next; 

					// update locations for next iteration 
					startRow += axis != Axis.ROW ? 1 : 0; 
					startCol += axis != Axis.COLUMN ? (axis == Axis.RL_DIAGONAL ? -1 : 1) : 0; 
					
					// update next to the next value 
					next = this.board[startRow][startCol]; 
				}
			} else break; 
		}

		return 0.0; 
	}

	/*
	the printBoard method prints the current state to the standard output
	*/
	public void printBoard() {
		// clear output ansi code 
		System.out.print("\033[H\033[2J");
		for (int i = 0; i < boardSize; i++) {
			printSep();
			printRow(i);
		}
		printSep();
	}
	
	/*
	the getBoxWidth method calculates the correct width of each box to print 
	*/
	private int getBoxWidth() {
		// maximum length of integer plus two spaces on each side for padding
		return (boardSize * boardSize + "").length() + 4; 
	}

	/*
	the printRow method prints a specific row of the board

	arguments:
		rowIndex - the row to print

	preconditions:
		rowIndex is a valid row  
	*/
	private void printRow(int rowIndex) {
		States[] row = this.board[rowIndex];
		printVert(); 
		for (int i = 0; i < boardSize; i++) {
			// if empty, print square number otherwise, print circle or cross 
			if (row[i] == States.EMPTY) {
				System.out.printf("|" + center(getSquareNum(rowIndex, i) + "", getBoxWidth()));
			} else {
				System.out.printf("|" + center(row[i] == States.CROSS ? CROSS : CIRCLE, getBoxWidth())); 
			}
		}
		System.out.println("|");
		printVert(); 
	}

	/*
	the center method centers a string in a string of some length

	arguments: 
		str - string to center
		finalLen - the length of the final centered string

	return values:
		String - a string of length finalLen with str centered 
	*/
	private String center(String str, int finalLen) {
		String finalStr = "  ";
		int maxLen = (boardSize * boardSize + "").length(); 
		// check if CROSS or CIRCLE because their escape sequences makes the string
		// longer than it is 
		int strLength = str.equals(CROSS) || str.equals(CIRCLE) ? 1 : str.length(); 

		// make up the length if str is shorter than maximum length possible 
		for (int i = 0; i < maxLen - strLength; i++) finalStr += " "; 

		finalStr += str + "  ";
		return finalStr; 
	}

	/*
	the printVert method prints rows consisting of only vertical bars 
	*/
	private void printVert() {
		for (int i = 0; i < boardSize; i++) {
			System.out.print("|");
			for (int ii = 0; ii < getBoxWidth(); ii++) {
				System.out.print(" "); 
			}
		}
		System.out.println("|"); 
	}

	/*
	the printSep method prints horizontal seperators 
	*/
	private void printSep() {
		for (int i = 0; i < boardSize; i++) {
			System.out.print("+");
			for (int ii = 0; ii < getBoxWidth(); ii++) {
				System.out.print("-"); 
			}
		}
		System.out.println("+"); 
	}

	/*
	the getSquareNum method returns a number for a specific index pair,
	i.e. the inverse of getIndexes 

	arguments:
		row - first index of the square 
		col - second index of the square 

	return values:
		int - one number that represents the square
	*/
	public int getSquareNum(int row, int col) {
		// base is the square number for the first square in each row, i.e. board[row][0]
		int base = row * boardSize + 1;
		return base + col;
	}

	/*
	the getIndexes method returns a index pair for a specific number,
	i.e. the inverse of getSquareNum

	arguments:
		squareNum - one number that represents the square 

	return values:
		int[] - first value is the first index of the square	
				second value is the second index of the square 
	*/
	public int[] getIndexes(int squareNum) {
		int[] indexes = new int[2];
		// integer truncation helps code determine the row 
		indexes[0] = (squareNum - 1) / boardSize;
		// calculate column by substracting the base (refer to comment in getSquareNum)
		// from the square number 
		indexes[1] = squareNum - (indexes[0] * boardSize + 1);
		return indexes;
	}

}