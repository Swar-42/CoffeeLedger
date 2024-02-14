import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OptionSelect {
    private Scanner scanner;
    private String title;
    private List<String> options;
    private String exitString;
    private int numOptions;
    private boolean titleAbove = true;

    public OptionSelect(Scanner scanner, String title, List<String> options, String exitString) {
        this.scanner = scanner;
        this.title = title;
        this.options = options;
        this.exitString = exitString;
        this.numOptions = options.size();
    }

    public OptionSelect(Scanner scanner, String title, int numOptions, String exitString) {
        this.scanner = scanner;
        this.title = title;
        this.options = new ArrayList<String>();
        this.exitString = exitString;
        this.numOptions = numOptions;
    }

    public int prompt() {
        while (true) {
            if (titleAbove) System.out.println(title);
            for (int i = 0; i < options.size(); i++) {
                System.out.println(i+1 + ") " + options.get(i));
            }
            System.out.println("0) " + exitString);
            if (!titleAbove) System.out.println(title);
            System.out.println("Enter a number between 1 and " + numOptions + ", or 0 to quit.");
            System.out.print("Selection: ");

            String input = scanner.nextLine();
            try {
                int selection = Integer.parseInt(input);
                if (selection <= numOptions) {
                    return selection;
                }
            } catch (NumberFormatException e) { }
            System.out.println("invalid input.");
            System.out.println();
        }
    }

    public void displayTitleAbove(boolean choice) {
        titleAbove = choice;
    }

    public static void main(String[] args) {
        List<String> options = new ArrayList<String>();
        options.add("option 1");
        options.add("option 2");
        options.add("this is the third one");
        OptionSelect test = new OptionSelect(new Scanner(System.in), "choose an option.", options, "exit the program");
        test.prompt();
    }
}
