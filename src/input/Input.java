package input;
import java.util.Scanner;

/**
 * Class which handles all basic input validation for a program (no other classes should use a Scanner on System.in).
 */
public class Input {
    // Scanner instance to use for all command-line input
    private static Scanner scanner = new Scanner(System.in);

    /**
     * validates and retrieves a user-input integer
     * @param prompt message to prompt the user with
     * @return a user-input integer
     */
    public static int getInteger(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            int output;
            try {
                output = Integer.parseInt(input);
                return output;
            } catch (NumberFormatException e) { }
            System.out.println("Invalid input (enter an integer).");
        }
    }

    /**
     * validates and retrieves a user-input non-whitespace String
     * @param prompt message to prompt the user with
     * @return a user-input non-whitespace String
     */
    public static String getString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (!input.matches("\\s*")) {
                return input;
            }
            System.out.println("Invalid input (enter a visible string).");
        }
    }

    /**
     * validates and retrieves a user-input double
     * @param prompt message to prompt the user with
     * @return a user-input double
     */
    public static double getDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            double output;
            try {
                output = Double.parseDouble(input);
                return output;
            } catch (NumberFormatException e) { }
            System.out.println("Invalid input (enter a decimal value).");
        }
    }

    /**
     * validates and retrieves a user-input integer
     * @param prompt message to prompt the user with
     * @return a user-input integer
     */
    public static double getPosDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            double output;
            try {
                output = Double.parseDouble(input);
                if (output >= 0) return output;
            } catch (NumberFormatException e) { }
            System.out.println("Invalid input (enter a positive decimal value).");
        }
    }
}
