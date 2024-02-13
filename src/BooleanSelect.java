import java.util.Scanner;

public class BooleanSelect {
    private String text;

    private static final String REGEX_YES = "(?i)^Y|YES";
    private static final String REGEX_NO =  "(?i)^N|NO";

    public BooleanSelect(String text) {
        this.text = text;
    }

    public Boolean prompt() {
        Scanner scanner = new Scanner(System.in);
        while (true) {

            System.out.println(text);
            System.out.print("Enter Y/N (yes/no): ");

            String input = scanner.nextLine();

            if (input.matches(REGEX_YES)) {
                scanner.close();
                return true;
            } else if (input.matches(REGEX_NO)) {
                scanner.close();
                return false;
            }
            System.out.println("Invalid input.");
        }
    }

    public static void main(String[] args) {
        BooleanSelect select = new BooleanSelect("is water wet?");
        System.out.println(select.prompt());
    }
}
