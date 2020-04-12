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
        Command.explainCommands();
        questions = new QuestionBank(postcode);

        System.out.println("I've hidden the treasure at my favourite " + placeType.name().toLowerCase().replaceAll("_", " ") + ". The questions below are a clue...\n");
        questions.printBank(false);

        String inputtedLineStr;
        String[] inputtedLineArr;
        String notes;
        boolean givenUp = false;
        while (true) {
            System.out.println("\nWhere's the treasure?");
            inputtedLineStr = input.nextLine();
            if (inputtedLineStr.strip().equalsIgnoreCase(placeName)) {
                System.out.println("Well done! That's correct\n");
                break;
            }

            inputtedLineArr = inputtedLineStr.strip().split(" ");
            boolean cmdTriggered = false;
            try {
                // switch statement wouldn't work...
                while (true) {
                    String toCompare = inputtedLineArr[0].strip();

                    if (toCompare.equalsIgnoreCase(Command.ANSWER.name())) {
                        cmdTriggered = true;
                        answerCmd(Integer.parseInt(inputtedLineArr[1]));
                        break;
                    }

                    if (toCompare.equalsIgnoreCase(Command.UPDATE.name())) {
                        cmdTriggered = true;
                        notes = inputtedLineArr[2];
                        for (int i = 3; i < inputtedLineArr.length; i++) {
                            notes += " ";
                            notes += inputtedLineArr[i];
                        }

                        updateCmd(Integer.parseInt(inputtedLineArr[1]), notes);
                        break;
                    }

                    if (toCompare.equalsIgnoreCase(Command.POSTCODE.name())) {
                        cmdTriggered = true;
                        postcodeCmd(inputtedLineArr[1]);
                        break;
                    }

                    if (inputtedLineStr.equalsIgnoreCase(Command.I_GIVE_UP.name().replaceAll("_", " "))) {
                        cmdTriggered = true;
                        givenUp = givenUpCmd();
                        break;
                    }

                    if (toCompare.equalsIgnoreCase(Command.HELP.name())) {
                        cmdTriggered = true;
                        helpCmd();
                        break;
                    }

                    if (toCompare.equalsIgnoreCase(Command.QUESTIONS.name())) {
                        cmdTriggered = true;
                        questions.printBank(false);
                        break;
                    }

                    break;
                }
            } catch (Exception e) {
                if (cmdTriggered) {
                    System.err.println("Command format is incorrect.");
                }
            }

            if (givenUp) {
                break;
            }

            if (!cmdTriggered) {
                System.out.println("It's not there...\n");
            }
        }
    }

    /** checks if a user is sure of their input **/
    private boolean isSure(String request) {
        String sure = "YES";
        System.out.println("Type '" + sure + "' if you definitely want to " + request + ". Type anything else to cancel: ");
        return (input.nextLine().equalsIgnoreCase(sure));
    }

    /** executes the ANSWER command **/
    private void answerCmd(int question) {
        if ((question > questions.numOfQuestions()) || (question <= 0)) {
            System.err.println("Inputted question does not exist.");
            return;
        }

        if (!isSure("see the answer to question " + question)) {
            System.out.println("Command cancelled.");
            return;
        }

        System.out.println("Answer to Q" + question + ": " + questions.getAnswer(question-1));
    }

    /** executes the UPDATE command **/
    private void updateCmd(int question, String notes) {
        if ((question > questions.numOfQuestions()) || (question <= 0)) {
            System.err.println("Inputted question does not exist.");
            return;
        }

        questions.updateNotes(question-1, notes);
        questions.printBank(false);
    }

    /** executes the POSTCODE command **/
    private void postcodeCmd(String postcode) {
        if (!isSure("check if " + postcode + "is the correct postcode")) {
            System.out.println("Command cancelled.");
            return;
        }

        if (postcode.strip().replaceAll(" ", "").equalsIgnoreCase(this.postcode)) {
            System.out.println("Postcode is correct (ignoring spacing).");
        } else {
            System.out.println("Postcode is incorrect.");
        }
    }

    /** executes the I_GIVE_UP command **/
    private boolean givenUpCmd() {
        if (!isSure("give up")) {
            return false;
        }
        System.out.println("\nBetter luck next time!\nHere are the answers: ");
        questions.printBank(true);
        System.out.println("Postcode: " + postcode);
        System.out.println(Character.toUpperCase(placeType.name().charAt(0)) + placeType.name().substring(1).toLowerCase() + ": " + placeName);
        return true;
    }

    /** executes the HELP command **/
    private void helpCmd() {
        Command.explainCommands();
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
}
