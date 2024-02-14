import java.util.ArrayList;
import java.util.List;

public class OptionSelect {
    private String title;
    private List<String> options;
    private String exitString;
    private int numOptions;
    private boolean titleAbove = true;

    public OptionSelect(String title, List<String> options, String exitString) {
        this.title = title;
        this.options = options;
        this.exitString = exitString;
        this.numOptions = options.size();
    }

    public OptionSelect(String title, int numOptions, String exitString) {
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

            int input = Input.getInteger("Selection: ");
            if (input <= numOptions) {
                return input;
            }
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
        OptionSelect test = new OptionSelect("choose an option.", options, "exit the program");
        test.prompt();
    }
}
