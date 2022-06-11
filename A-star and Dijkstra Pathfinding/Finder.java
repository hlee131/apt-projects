import java.io.*; 
import java.util.*; 

public class Finder {

    // if path can be diagonal 
    private final boolean DIAGONAL = false; 

    // heuristic functions 
    public final Heuristic DIJKSTRA = new Heuristic() {
        public double h(Square s, int[] e) { return 0; }
    }; 

    // A* heuristics 
    public final Heuristic EUCLIDEAN = new Heuristic() {
        public double h(Square s, int[] e) {
            return Math.sqrt(Math.pow((s.col - e[1]), 2) + Math.pow((s.row - e[0]), 2)); 
        }
    }; 

    public final Heuristic MANHATTAN = new Heuristic() {
        public double h(Square s, int[] e) {
            return Math.abs(s.col - e[1]) + Math.abs(s.row - e[0]); 
        }
    }; 

    // chosen heuristic to use
    public Heuristic HEURISTIC = DIJKSTRA; 

    // ansi codes
    public String CLEARCODE = "\033[H\033[3J", 
                   WALLCODE = "\u001b[31m", 
                   OPENCODE = "\u001b[32m", 
                   STARTCODE = "\u001b[35m", 
                   ENDCODE = "\u001b[35m", 
                   PATHCODE = "\u001b[37m",
                   VISITCODE = "\u001b[36m",
                   RESET = "\u001b[0m"; 

    // char allows for more efficient setting and getting
    private ArrayList<char[]> field; 

    private int[] start = { -1, -1 };
    private int[] end = { -1, -1 }; 

    public Finder(String fn) {
        int row = 0;
        try {
            Scanner file = new Scanner(new File(fn));
            field = new ArrayList<char[]>(); 
            while (file.hasNext()) { 
                String line = file.next(); 
                if (line.contains("S")) { start[0] = row; start[1] = line.indexOf('S'); }
                if (line.contains("E")) { end[0] = row; end[1] = line.indexOf('E'); } 
                field.add(line.toCharArray());
                row++; 
            }
        } catch (FileNotFoundException e) {
            System.out.printf("error: file %s not found", fn);    
            System.exit(1); 
        }
        
        if (start[0] == -1 || end[0] == -1) {
            System.out.println("error: both start and end need to be defined");
            System.exit(1); 
        }
    }
    
    public Finder(int rows, int col) { randomField(rows, col); }

    public Finder() { 
        Scanner in = new Scanner(System.in);
        String answer; 
        do {
            randomField(20, 70); 
            printField();
            System.out.print(RESET); 
            System.out.print("\nRegenerate (y/n)? "); 
            answer = in.next();
            while (!answer.equals("y") && !answer.equals("n")) { 
                System.out.print("y or n: "); 
                answer = in.next(); 
            }
        } while (answer.equals("y")); 
    }
    
    public void printField() {
        System.out.print(CLEARCODE); 
        for (char[] row : this.field) {
            String coloredRow = ""; 
            for (int i = 0; i < row.length; i++) {
                if (coloredRow.length() != 0 && 
                    coloredRow.charAt(coloredRow.length() - 1) == row[i]) {
                    coloredRow += row[i];
                    continue; 
                }
                
                switch (row[i]) {
                    case 'O': coloredRow += OPENCODE + "O"; break;    // open space
                    case 'X': coloredRow += WALLCODE + "X"; break;    // wall
                    case 'S': coloredRow += STARTCODE + "S"; break;   // start point   
                    case 'E': coloredRow += ENDCODE + "E"; break;     // end point  
                    case '*': coloredRow += PATHCODE + "*"; break;    // path selected 
                    case '.': coloredRow += VISITCODE + "."; break;      // visited
                }    
            }
            System.out.println(coloredRow); 
        }
    }
    
    private void randomField(int rows, int cols) {
        // generate random start and end poitns 
        this.start[0]= (int) (Math.random() * rows);
        this.start[1] = (int) (Math.random() * cols); 
        this.end[0] = (int) (Math.random() * rows); 
        this.end[1] = (int) (Math.random() * cols); 
        
        this.field = new ArrayList<>(); 
        
        for (int i = 0; i < rows; i++) {
            String row = ""; 
            for (int j = 0; j < cols; j++) {
                if (i == start[0] && j == start[1]) row += "S"; 
                else if (i == end[0] && j == end[1]) row += "E"; 
                // 30% chance of it being a wall 
                else if (Math.random() < 0.3) row += "X";
                else row += "O"; 
            }
            field.add(row.toCharArray()); 
        }
    }
    
    // best first search with heuristics
    public void solve() { 
        // list of squares to check 
        ArrayList<Square> frontier = new ArrayList<>();  
        addVertices(frontier, new Square(start[0], start[1], 0, null)); 
        while (true) {
            printField();

            // if no more frontier nodes, path not found 
            if (frontier.size() == 0) {
                System.out.println("Path not found");
                return; 
            }

            double bestEval = frontier.get(0).dist + HEURISTIC.h(frontier.get(0), this.end);
            int bestIndex = 0; 
            for (int i = 0; i < frontier.size(); i++) {
                Square s = frontier.get(i); 
                // check if path found in frontier
                if (s.row == end[0] && s.col == end[1]) {
                    // mark each path with parent square 
                    Square curr = s.parentSq; 
                    do 
                        if (field.get(curr.row)[curr.col] != 'S')
                            field.get(curr.row)[curr.col] = '*';
                    while ((curr = curr.parentSq) != null);     
                    printField(); 

                    // cleanup 
                    for (char[] arr : field) 
                        for (int j = 0; j < arr.length; j++)
                            arr[j] = arr[j] != 'X' && arr[j] != 'S' && arr[j] != 'E' ? 
                                'O' : 
                                arr[j]; 
                    
                    return; 
                // check if this node is better 
                } else if (s.dist + HEURISTIC.h(s, this.end) < bestEval) { 
                    bestEval = s.dist + HEURISTIC.h(s, this.end); bestIndex = i; 
                }
            }
            // remove the best node and add its adjacent nodes
            addVertices(frontier, frontier.get(bestIndex)); 
            frontier.remove(bestIndex); 
        }
    }

    private void addVertices(ArrayList<Square> f, Square c) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                // if diagonals not allowed, row or col must be the same as existign 
                if (!DIAGONAL && i != 0 && j != 0) continue; 
                int newRow = c.row + i; 
                int newCol = c.col + j; 
                // check valid square 
                // check not visited already 
                if (newRow >= 0 && newCol >= 0 && 
                    newRow < field.size() && newCol < field.get(0).length && 
                    (field.get(newRow)[newCol] == 'O' || 
                     field.get(newRow)[newCol] == 'E')) {
                        // add new square onto frontier 
                        f.add(new Square(newRow, newCol, c.dist + 1, c)); 
                        // mark lcoation as visited 
                        if (field.get(newRow)[newCol] != 'E')
                            field.get(newRow)[newCol] = '.'; 
                    }
            }
        }
    }
}

class Square {
    public int row, col, dist; 

    // use a linked list style to track shortest path 
    public Square parentSq; 

    public Square(int r, int c, int d, Square ps) {
        row = r; col = c; dist = d; parentSq = ps; 
    }

    public String toString() {
        return String.format("%d, %d %d", row, col, dist);
    }
}

// interface to store a heuristic function 
interface Heuristic {
    public double h(Square s, int[] e); 
}