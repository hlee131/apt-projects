import java.util.Scanner; 

class Main {
    public static Finder f = null; 
    public static Scanner in = new Scanner(System.in);
    
  public static void main(String[] args) {

    while (true) menu(); 

  }

    public static void menu() { 
        System.out.print("\u001b[0m");
        System.out.println("Choose an option:");
        System.out.println("1. Choose a file to import from");
        System.out.println("2. Generate a random field");
        System.out.println("3. Set heuristic function"); 
        System.out.println("4. Solve");
        System.out.println("5. Exit"); 

        char choice;
        
        do {
            System.out.print("Option (1/2/3/4/5): ");
            choice = in.next().charAt(0);
        } while (choice != '1' && choice != '2' && choice != '3' && choice != '4' && choice != '5');

        System.out.print("\033[H\033[0J"); 

        switch (choice) {
            case '1': 
                System.out.print("Filename: "); 
                String fn = in.next();
                f = new Finder(fn); 
                break; 
            case '2': f = new Finder(); break; 
            case '3':
                do {
                    System.out.print("Heuristic (D/E/M): ");
                    choice = in.next().charAt(0); 
                } while (choice != 'D' && choice != 'E' && choice != 'M'); 
                if (choice == 'D') f.HEURISTIC = f.DIJKSTRA; 
                else if (choice == 'E') f.HEURISTIC = f.EUCLIDEAN;
                else if (choice == 'M') f.HEURISTIC = f.MANHATTAN; 
                break; 
            case '5': in.close(); System.exit(0); 
            case '4': 
                if (f == null) {
                    System.out.println("No field yet"); return; 
                }
                f.solve(); break; 
            default: break; 
        }
    }
}