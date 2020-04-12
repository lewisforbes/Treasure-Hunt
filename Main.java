import java.util.Scanner;

public class Main {
    public static Difficulty difficulty = Difficulty.MEDIUM;
    public static final String exampleKeyword = "EXAMPLE";
    public static final Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Game settings: ");
        String apiKey = getAPIKey();
        difficulty = getDifficulty();

        System.out.println("\nWelcome to Treasure Hunt!\nUsing Google is (really) encouraged.");
        new Round(apiKey);
        System.out.println("\nThanks for playing!\n\n");
    }

    /** gets API Key or lets user run example **/
    private static String getAPIKey() {
        String inputtedLine;
        String interpreted = "";
        String yesKeyword = "CONTINUE";

        while (true) {
            System.out.println("Enter Google Maps Places API key or type '" + exampleKeyword + "' if you've not got one.");
            inputtedLine = input.nextLine();

            if (inputtedLine.strip().toUpperCase().equals(exampleKeyword)) {
                interpreted = "the EXAMPLE command";
            } else {
                interpreted = "an API key";
            }

            System.out.println("This was interpreted as " + interpreted + ". Enter '" + yesKeyword + "' to confirm, or anything else to cancel.");;
            if (input.nextLine().strip().equalsIgnoreCase(yesKeyword)) {
                break;
            }
            System.out.println("Cancelled.\n");
        }

        return inputtedLine;
    }

    /** prompts user for difficulty and validates it **/
    private static Difficulty getDifficulty() {
        String inputtedLine;
        Difficulty[] difficultiesValues = Difficulty.values();
        String difficultiesStr = "";

        for (Difficulty difficulty : difficultiesValues) {
            difficultiesStr += difficulty.name() + ", ";
        }
        difficultiesStr = difficultiesStr.substring(0, difficultiesStr.length()-2);

        while(true) {
            System.out.println("\nEnter one of the following to select a difficulty: <" + difficultiesStr + ">");
            inputtedLine = input.nextLine();

            for (Difficulty difficulty : difficultiesValues) {
                if (difficulty.name().strip().equalsIgnoreCase(inputtedLine.strip())) {
                    System.out.println(difficulty.name() + " selected");
                    return difficulty;
                }
            }
            System.err.println("Invalid input");
        }
    }
}