import java.util.Scanner;

public class Main {
    public static Difficulty difficulty = Difficulty.MEDIUM;

    public static void main(String[] args) {
        System.out.println("Enter API key: ");
        Scanner input = new Scanner(System.in);
        String apiKey = input.nextLine();
        difficulty = getDifficulty(input);

        System.out.println("\nWelcome to Treasure Hunt!\nUsing Google is (really) encouraged.\n");
        new Round(apiKey);
        System.out.println("\nThanks for playing!\n\n");
    }

    /** prompts user for difficulty and validates it **/
    private static Difficulty getDifficulty(Scanner input) {
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