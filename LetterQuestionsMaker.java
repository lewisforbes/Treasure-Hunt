import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

public class LetterQuestionsMaker {

    private static final String[] illegalWords = new String[] {"which", "many", "except", "common"};

    /** generates a list of questions with answers following a given string of letters **/
    public static ArrayList<Question> mkQsFromGroup(String letters) {
        String toFind = letters;
        Question[] outputArray = new Question[letters.length()];
        ArrayList<Question> currentQuestions;
        int charToRemove = -1;
        ArrayList<Question> questionsToRemove;

        while(toFind.replaceAll(" ", "").length() != 0) {
            currentQuestions = getLetterQuestions();
            questionsToRemove = new ArrayList<>();

            for (Question question : currentQuestions) {
                charToRemove = -1;
                for (int c = 0; c < toFind.length(); c++) {
                    // if first letter of question equals current letter
                    if (Character.toUpperCase(question.getCorrectAnswer().charAt(0)) == Character.toUpperCase(toFind.charAt(c))) {
                        outputArray[c] = question;
                        questionsToRemove.add(question);
                        charToRemove = c;
                        break;
                    }
                }
                if (charToRemove != -1) {
                    toFind = replaceWithSpace(charToRemove, toFind);
                }
            }

            for (Question q : questionsToRemove) {
                currentQuestions.remove(q);
            }
        }

        ArrayList<Question> outputList = new ArrayList<>();
        for (Question q : outputArray) {
            outputList.add(q);
        }
        return outputList;
    }

    /** replaces the character at given index in a given string with a space **/
    private static String replaceWithSpace(int index, String str) {
        if ((index+1) < str.length()) {
            return str.substring(0, index) + " " + str.substring(index+1);
        } else {
            return str.substring(0,index) + " ";
        }
    }

    /** generates a list of questions from web **/
    private static ArrayList<Question> getLetterQuestions() {
        int questionsToFind = 20;
        String rawText = webpageToStr("https://opentdb.com/api.php?amount=" + questionsToFind + "&type=multiple&encode=base64&difficulty=" + Main.difficulty.name().toLowerCase());

        int charsAfterKeyword = 3;
        String questionKeyword = "question";
        String correctKeyword = "correct_answer";
        String incorrectKeyword = "incorrect_answers";
        int questionIndex;
        int correctIndex;
        int incorrectIndex;

        String currentQuestion;
        String currentCorrect;
        String[] currentIncorrect;

        ArrayList<Question> output = new ArrayList<>();

        while (true) {
            questionIndex = rawText.indexOf(questionKeyword) + questionKeyword.length() + charsAfterKeyword;
            correctIndex = rawText.indexOf(correctKeyword) + correctKeyword.length() + charsAfterKeyword;
            incorrectIndex = rawText.indexOf(incorrectKeyword) + incorrectKeyword.length() + (charsAfterKeyword+1);

            currentQuestion = getString(questionIndex, rawText);
            currentCorrect = getString(correctIndex, rawText);
            currentIncorrect = getArray(incorrectIndex, rawText);

            if ((currentQuestion != null) && (currentCorrect != null) && (currentIncorrect != null)) {
                if ((!currentCorrect.contains(",")) && questionValid(currentQuestion)) {
                    output.add(new Question(currentQuestion.stripLeading(), currentCorrect.stripLeading(), currentIncorrect));
                }
            }

            rawText = rawText.substring(incorrectIndex);

            if (rawText.indexOf(questionKeyword) == -1) {
                break;
            }
        }

        return output;
    }

    /** checks a question is valid by ensuring it doesn't contain certiain words **/
    private static boolean questionValid(String question) {
        for (String word : illegalWords) {
            if (question.toLowerCase().contains(word.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    /** parses a specific array from raw webpage data **/
    private static String[] getArray(int index, String text) {
        int endOfStr = text.indexOf("]", index) - 1;
        String[] output = text.substring(index, endOfStr).split(",");
        for (int i=0; i<output.length; i++) {
            output[i] = decodeB64String(output[i].replaceAll("\"", ""));
        }
        return output;
    }

    /** parses a specific string from raw webpage data **/
    private static String getString(int index, String text) {
        int endOfStr = text.indexOf("\"", index);
        String output;
        try {
            output = decodeB64String(text.substring(index, endOfStr));
        } catch (IllegalArgumentException e1) {
            try {
                output = decodeB64String(text.substring(index, endOfStr-2)) + "?";
            } catch (IllegalArgumentException e2) {
                output = null;
            }

        }

        return output;
    }

    /** gets text from a webpage **/
    private static String webpageToStr(String urlStr) {
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

    /** converts a base 64 string into standard text **/
    private static String decodeB64String(String base64Str) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Str);
            return new String(decodedBytes);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}