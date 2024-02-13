import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OptionSelect {
    private String title;
    private List<String> options;
    private String exitString;

    public OptionSelect(String title, List<String> options, String exitString) {
        this.title = title;
        this.options = options;
        this.exitString = exitString;
    }

    public int prompt() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println(title);
            for (int i = 0; i < options.size(); i++) {
                System.out.println(i+1 + ") " + options.get(i));
            }
            System.out.println("0) " + exitString);
            System.out.println("Enter a number between 1 and " + options.size() + ", or 0 to " + exitString + ".");
            System.out.print("Selection: ");

            String input = scanner.nextLine();
            
            try {
                int selection = Integer.parseInt(input);
                if (selection <= options.size()) {
                    scanner.close();
                    return selection;
                }
            } catch (NumberFormatException e) { }
            System.out.println("invalid input.");
        }
    }

    public static void main(String[] args) {
        List<String> options = new ArrayList<String>();
        options.add("option 1");
        options.add("option 2");
        options.add("this is the third one");
        OptionSelect test = new OptionSelect("choose an option.", options, "exit the program");
        test.prompt();
    }
}
