import java.util.Scanner; 

// class to play tic tac toe games 
class TicTacToe {

	// state that persists between multiple tic tac toe games 
	private String player0, player1;
	private double[] totalScore; 
	private int boardSize; 
	private Scanner in; 
	private boolean singlePlayer; 
	/*
	construct a new tictactoe game object by asking the user for settings, i.e usernames 
	*/
	public TicTacToe() {

		totalScore = new double[2]; 

		// ask for settings 
		// checking that two usernames are not identical is not neccessary because 
		// turns, scores, etc. are all based on 0 or 1 instead of usernames 
		in = new Scanner(System.in); 

        System.out.println("Type 1 for Single Player or 2 for Multiplayer: ");

        String singleOrMulti = in.next(); 

        while (!singleOrMulti.equals("1") && !singleOrMulti.equals("2")) {
            System.out.println("Invalid input (1 or 2): ");
            singleOrMulti = in.next();
        }

        singlePlayer = singleOrMulti.strip().equals("1");

		System.out.print("Player 1 Username: ");
		player0 = in.next();
        if (!singlePlayer) {
            System.out.print("Player 2 Username: ");
            player1 = in.next(); 
        } else player1 = "Bot (AI)";
		System.out.print("Board Size: ");
		boardSize = Helper.nextInt(in); 

		// board size validation 
		while (boardSize <= 1) {
			System.out.print("Board size must be integer above 1: ");
			boardSize = Helper.nextInt(in); 
		}
	}

	/*
	playTicTacToe method runs the game loop so that multiple tic tac toe games can be played 
	*/
	public void playTicTacToe() {
		
		while (true) {  
			// create new game
			Game game = new Game(player0, player1, boardSize, singlePlayer);
			double score = game.run();

			// update total score 
			// if score is negative, player 0 won the game, otherwise player 1 won the game
			// 0.5 is returned if it was a tie game 
			if (score == 0.5) {
				totalScore[0] += 0.5; 
				totalScore[1] += 0.5;
			} else totalScore[score < 0 ? 0 : 1] += Math.abs(score);

			// check if game over 
			System.out.print("Play again (y/n)? ");
			String again = in.next();
			
			// ask to create new game
			while (!(again.equals("y") || again.equals("n"))) {
				System.out.print("Please input y or n: ");
				again = in.next();
			}

			// exit game loop if user chose not to play again
			if (again.equals("n")) break; 
		}
		
		// output final game message 
		outputFinalMessage();
		in.close(); 
	}

	/*
	the outputFinalMessage method outputs a statement declaring a winner or a tie depending 
	on the state of the game 
	*/
	private void outputFinalMessage() {
		if (totalScore[0] == totalScore[1]) System.out.println("It was a tie!");
		else System.out.printf("\u001b[35m%s won!\u001b[0m\n", totalScore[0] > totalScore[1] ? player0 : player1); 

		System.out.printf("\u001b[35mFINAL SCORE: %s %.2f - %.2f %s\u001b[0m\n", player0, totalScore[0], totalScore[1], player1); 
	}
}