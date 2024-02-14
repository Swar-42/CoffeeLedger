import java.util.Scanner;

public class Input {
    
    private static Scanner scanner = new Scanner(System.in);

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
}
