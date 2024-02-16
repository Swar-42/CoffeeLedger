package input;
import java.util.ArrayList;
import java.util.List;

/**
 * describes a class for getting a user's decision between an enumerated amount of options.
 */
public class OptionSelect {
    // title to prompt the user with
    private String title;
    // list of options for the user to select from
    private List<String> options;
    // string to display as the exit/back option
    private String exitString;
    // the size of options
    private int numOptions;
    // display the title text above or below the list of options
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

    /**
     * prompts the user with a number of options and returns their validated input.
     * @return user's valid choice between a number of options
     */
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

    /**
     * set the object to display its title above or below the list of options
     * @param choice
     */
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
