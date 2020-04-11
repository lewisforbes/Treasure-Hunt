import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class NumberQuestionMaker {

    private static String failedReturn = "FAILED";

    /** returns a list of number questions from a string of numbers **/
    public static ArrayList<Question> mkQsFromGroup(String numbers) {
        ArrayList<String> foundQuestions = new ArrayList<>();
        ArrayList<String> foundQuestionsAnswers = new ArrayList<>();
        ArrayList<Question> output = new ArrayList<>();

        String[] groupedInFours = groupStr(numbers, 4);

        String potQuestion;
        String potQuestion1;
        String potQuestion2;
        String currentNum;
        String currentNum1;
        String currentNum2;

        for (String group : groupedInFours) {
            potQuestion = null;
            potQuestion1 = null;
            potQuestion2 = null;

            currentNum1 = null;
            currentNum2 = null;

            // ideal situation
            currentNum = group;
            if (group.length() == 4) {
                potQuestion = getNumberQuestion("year", currentNum);
            } else {
                potQuestion = getNumberQuestion("trivia", currentNum);
            }

            // second best - breaks down number into two digits then the rest
            if (!questionFound(potQuestion)) {
                currentNum1 = group.substring(0, 2);
                potQuestion1 = getNumberQuestion("trivia", currentNum1);

                currentNum2 = group.substring(2);
                potQuestion2 = getNumberQuestion("trivia", currentNum2);

                if ((!questionFound(potQuestion1) || (!questionFound(potQuestion2)))) {
                    throw new IllegalArgumentException("Was unable to generate question with number length 2 or 1");
                }
            }
            if (potQuestion1 == null) {
                foundQuestions.add(potQuestion);
                foundQuestionsAnswers.add(currentNum);
            } else {
                foundQuestions.add(potQuestion1);
                foundQuestions.add(potQuestion2);
                foundQuestionsAnswers.add(currentNum1);
                foundQuestionsAnswers.add(currentNum2);
            }

        }

        for (int i = 0; i < foundQuestions.size(); i++) {
            output.add(new Question(foundQuestions.get(i), foundQuestionsAnswers.get(i)));
        }

        return output;
    }

    /** checks if a number question was successfully created **/
    private static boolean questionFound(String potQuestion) {
        return !potQuestion.equals(failedReturn);
    }

    /** gets a question of given type for given number **/
    private static String getNumberQuestion(String type, String number) {
        String url = "http://www.numbersapi.com/" + number + "/" + type + "?default=" + failedReturn;
        String question = webpageToStr(url);
        question = "What" + question.substring(number.length());

        if ((question.charAt(question.length() - 1) == '.') || (question.charAt(question.length() - 1) == '!')) {
            question = question.substring(0, question.length() - 1) + "?";
        } else {
            question += "?";
        }

        return question;
    }

    /** groups a string into groups of specified size. The final group will be smaller than the group size if applicable **/
    private static String[] groupStr(String str, int groupIn) {
        String output = "";
        String divChar = "-";

        for (int c = 1; c <= str.length() - 1; c++) {
            output += str.charAt(c - 1);
            if ((c % groupIn == 0) && (c != (str.length() - 1))) {
                output += divChar;
            }
        }

        output += str.charAt(str.length() - 1);
        return output.split(divChar);

    }

    /** gets content from a plain text website **/
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
}
