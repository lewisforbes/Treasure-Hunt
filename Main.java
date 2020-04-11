import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Enter API key: ");
        Scanner input = new Scanner(System.in);
        String apiKey = input.nextLine();

        System.out.println("\nWelcome to Treasure Hunt!\nUsing Google is (really) encouraged!\n");
        new Round(apiKey);
    }
}