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
        System.out.println(title);
        for (int i = 0; i < options.size(); i++) {
            System.out.println(i+1 + ") " + options.get(i));
        }
        System.out.println("0) " + exitString);
        System.out.println("Enter a number between 1 and " + options.size() + ", or 0 to " + exitString + ".");
        System.out.println("Selection: ");

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        scanner.close();
        
        try {
            int selection = Integer.parseInt(input);
            if (selection <= options.size()) {
                return selection;
            }
        } catch (NumberFormatException e) { }
        System.out.println("Please enter a number between 0 and " + options.size() + ".");
        return prompt();
    }
}
