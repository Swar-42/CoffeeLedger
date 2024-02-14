import java.util.Scanner;

public class BooleanSelect {
    private Scanner scanner;
    private String text;

    private static final String REGEX_YES = "(?i)^Y|YES";
    private static final String REGEX_NO =  "(?i)^N|NO";

    public BooleanSelect(Scanner scanner, String text) {
        this.scanner = scanner;
        this.text = text;
    }

    public Boolean prompt() {
        while (true) {
            System.out.println(text);
            System.out.print("Enter Y/N (yes/no): ");

            String input = scanner.nextLine();

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
        BooleanSelect select = new BooleanSelect(new Scanner(System.in), "is water wet?");
        System.out.println(select.prompt());
    }
}
