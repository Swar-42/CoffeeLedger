package input;

/**
 * Describes a simple mechanism for Yes/No prompting and recieving that input
 */
public class BooleanSelect {
    // text to prompt the user with at command-line.
    private String text;

    // regular expressions to match yes and no with
    private static final String REGEX_YES = "(?i)^Y|YES";
    private static final String REGEX_NO =  "(?i)^N|NO";

    public BooleanSelect(String text) {
        this.text = text;
    }

    /**
     * prompts the user with a yes/no question and returns their response
     * @return true if yes, false if no.
     */
    public Boolean prompt() {
        while (true) {
            System.out.println(text);

            String input = Input.getString("Enter Y/N (yes/no): ");

            if (input.matches(REGEX_YES)) {
                return true;
            } else if (input.matches(REGEX_NO)) {
                return false;
            }
            System.out.println("Invalid input.");
            System.out.println();
        }
    }

    public static void main(String[] args) {
        BooleanSelect select = new BooleanSelect("is water wet?");
        System.out.println(select.prompt());
    }
}
