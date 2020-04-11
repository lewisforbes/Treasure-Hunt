import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class Round {
    private String postcode;
    private PlaceTypes placeType;
    private QuestionBank questions;
    private String placeName;
    private boolean searchedForPlaceName = false;
    private String apiKey;
    private Scanner input = new Scanner(System.in);

    public Round(String apiKey) {
        this.apiKey = apiKey;
        this.postcode = getRandomPostcode();
        this.placeType = PlaceTypes.getRandomPlaceType();
        this.placeName = getPlaceName();
        if (placeName.equals("dates")) {
            System.err.println("Error: API key is invalid. Restart the program and try again");
        } else {
            System.out.println("Form a postcode from the first letter or full number for the answers to the following questions.");
            play();
        }
    }

    /** gets a random UK postcode **/
    private String getRandomPostcode() {
        String rawData = webpageToStr("http://api.postcodes.io/random/postcodes");
        String keyWord = "postcode";
        return parseFromRawData(keyWord, rawData, "\"", 3);
    }

    /** gets text from a webpage **/
    private String webpageToStr(String urlStr) {
        try {
            //Instantiating the URL class
            URL url = new URL(urlStr);
            //Retrieving the contents of the specified page
            Scanner sc = new Scanner(url.openStream());
            //Instantiating the StringBuffer class to hold the result
            StringBuffer sb = new StringBuffer();
            while (sc.hasNext()) {
                sb.append(sc.next() + " ");
            }
            //Retrieving the String from the String Buffer object
            String result = sb.toString();
            //Removing the HTML tags
            result = result.replaceAll("<[^>]*>", "");
            result = result.substring(0, result.length() - 1);
            return result;
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to retrieve data.");
        }

    }

    /** begins a game **/
    private void play() {
        String giveUp = "I give up";
        System.out.println("I've hidden the treasure at my favourite " + placeType.name().toLowerCase().replaceAll("_", " ") + ". The questions below are a clue...\nIf you're stuck, type in a question number for its answer, or '" + giveUp + "' to give up.\n");
        questions = new QuestionBank(postcode);
        questions.printBank(false);

        String inputtedLine;
        while (true) {
            System.out.println("Where's the treasure?");
            inputtedLine = input.nextLine();

            try {
                answerRequest(Integer.parseInt(inputtedLine.strip()));
            } catch (NumberFormatException e) {
                if (inputtedLine.strip().equalsIgnoreCase(giveUp)) {
                    givenUp();
                    break;
                }
                if (inputtedLine.strip().equalsIgnoreCase(placeName.strip())) {
                    System.out.println("Well done!");
                    break;
                }
            }
        }
    }

    /** checks user is sure and then provides answer to specified question **/
    private void answerRequest(int questionNum) {
        if (questionNum > questions.numOfQuestions()) {
            System.err.println("You have requested the answer to a question that doesn't exist");
            return;
        }

        System.out.println("Enter 'YES' if you are you sure you'd like the answer to Q" + questionNum + ":");
        String inputtedLine = input.nextLine();

        if (inputtedLine.equalsIgnoreCase("YES")) {
            System.out.println(questions.getAnswer(questionNum-1));
        }

        System.out.print("\n");
    }

    /** gets place name from Google API **/
    private String getPlaceName() {
        if (searchedForPlaceName) {
            throw new IllegalArgumentException("Place name has been searched for more than once.");
        }

        String[] latLong = getLatLong();
        String url = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=" +
                placeType.name().toLowerCase() + "&inputtype=textquery&key=" + apiKey +
                "&locationbias=circle:2000@" + latLong[0] + "," + latLong[1] + "&fields=name";

        String rawData = webpageToStr(url);
        searchedForPlaceName = true;
        return parseFromRawData("name", rawData, "\"", 5);
    }

    /** gets latitude and longitude of postcode **/
    private String[] getLatLong() {
        String rawData = webpageToStr("http://api.postcodes.io/postcodes/" + postcode.replace(" ", "%20"));
        String[] output = new String[2];
        output[0] = parseFromRawData("latitude", rawData, ",", 2);
        output[1] = parseFromRawData("longitude", rawData, ",", 2);
        return output;
    }

    /** parses text from a raw data **/
    private String parseFromRawData(String keyWord, String rawData, String stopAt, int buffer) {
        int startOfData = rawData.indexOf(keyWord) + keyWord.length() + buffer;
        int endOfData = rawData.substring(startOfData).indexOf(stopAt) + startOfData;
        return rawData.substring(startOfData, endOfData);
    }

    /** method to run when player has given up **/
    private void givenUp() {
        System.out.println("\nBetter luck next time!\nHere are the answers: ");
        questions.printBank(true);
        System.out.println("Postcode: " + postcode);
        System.out.println(Character.toUpperCase(placeType.name().charAt(0)) + placeType.name().substring(1).toLowerCase() + ": " + placeName);
    }
}
