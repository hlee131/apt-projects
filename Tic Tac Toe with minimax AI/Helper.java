import java.util.Scanner; 

// helper class for input validation 
public class Helper {

	/*
	the isInt method checks if a string is an integer

	arguments:
		str - string to checl

	return values:
		true - is a string
		false - is not a string 
	*/
	public static boolean isInt(String str) {
		try {
			Integer.parseInt(str);
			return true; 
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/*
	the nextInt method repeatedly prompts for an integer until one is given

	arguments:
		in - scanner to use to take input 

	return values:
		int - input that is an integer
	*/
	public static int nextInt(Scanner in) {
		// can't use nextInt because can't do continuos loop with try/catch
		String input = in.next();
		while (!isInt(input)) {
			System.out.print("Expected an integer: "); 
			input = in.next(); 
		}
		return Integer.parseInt(input); 
	}
}