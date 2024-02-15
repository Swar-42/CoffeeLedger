package input;
public class BooleanSelect {
    private String text;

    private static final String REGEX_YES = "(?i)^Y|YES";
    private static final String REGEX_NO =  "(?i)^N|NO";

    public BooleanSelect(String text) {
        this.text = text;
    }

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
