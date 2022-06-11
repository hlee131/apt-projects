import java.util.*; 

// uses minimax: https://en.wikipedia.org/wiki/Minimax#/media/File:Minimax.svg
public class Bot {

    private final int MAX_DEPTH;

    // board 
    private Game g; 

    public Bot(Game g) {
        this.g = g; 
        this.MAX_DEPTH = (int) Math.pow((double) g.getBoardSize(), 2.0);
    }

    public int nextMove() {
        return iterateOverMoves(0, true, new AlphaBetaPair()).move;
    }

    // two types of pairs 
    // [ alpha: [eval, move], beta: [eval, move] ] returned by each node
    private MoveEvalPair iterateOverMoves(int depth, boolean maximize, AlphaBetaPair ab) {
        if (depth == MAX_DEPTH || g.findWinner() != 0 || g.boardFull()) 
            return new MoveEvalPair(-1, evalFunction(g.board, depth)); 
        else {
            ArrayList<Integer> possibleMoves = possibleMoves(g.board);
            // iterate over subtrees, i.e. possible moves 
            for (Integer move : possibleMoves) {
                int[] coords = g.getIndexes(move);
                // modify board to create subtree
                g.board[coords[0]][coords[1]] = maximize ? Game.States.CROSS : Game.States.CIRCLE;
                MoveEvalPair results = iterateOverMoves(depth + 1, !maximize, ab.clone());
                // backtrack
                g.board[coords[0]][coords[1]] = Game.States.EMPTY;
                if (maximize && ab.a.eval < results.eval) {
                    ab.a.eval = results.eval; ab.a.move = move;  
                } else if (!maximize && ab.b.eval > results.eval) {
                    ab.b.eval = results.eval; ab.b.move = move; 
                }

                // check for pruning
                if (ab.a.eval >= ab.b.eval) break; 
            }
        }

        return maximize ? ab.a : ab.b; 
    }

    // return an array of empty squares
    private ArrayList<Integer> possibleMoves(Game.States[][] board) {
        ArrayList<Integer> possible = new ArrayList<>(); 

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == Game.States.EMPTY) 
                    possible.add(g.getSquareNum(i, j)); 
            }
        }

        return possible; 
    }

    // basic evaluation function looking at win cases 
    private double evalFunction(Game.States[][] board, int depth) {
        double win = g.findWinner(); 
        if (win == 0.0) return 0.0; 
        else if (win > 0.0) return 100.0 - depth; 
        else return -100.0 + depth; 
    }
}

// container classes for values 
class AlphaBetaPair {
    public MoveEvalPair a;
    public MoveEvalPair b; 

    public AlphaBetaPair() {
        this.a = new MoveEvalPair();
        this.b = new MoveEvalPair();
        this.a.move = -1; 
        this.b.move = -1;
        // start each player at worst possible evaluation 
        this.a.eval = Double.NEGATIVE_INFINITY;
        this.b.eval = Double.POSITIVE_INFINITY; 
    }

    public String toString() {
        return String.format("a: (%d, %f) b: (%d, %f)", this.a.move, this.a.eval, this.b.move, this.b.eval); 
    }

    public AlphaBetaPair clone() {
        AlphaBetaPair newAB = new AlphaBetaPair();
        newAB.a = new MoveEvalPair(this.a.move, this.a.eval);
        newAB.b = new MoveEvalPair(this.b.move, this.b.eval);
        return newAB; 
    }
}

class MoveEvalPair {
    public int move;
    public double eval; 

    public MoveEvalPair(int move, double eval) {
        this.move = move; this.eval = eval; 
    }

    public MoveEvalPair() {}

    public String toString() {
        return String.format("Move: %d, Eval: %f", move, eval);
    }
}