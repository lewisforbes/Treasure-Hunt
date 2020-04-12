import java.util.ArrayList;

public class QuestionBank {
    private ArrayList<Question> questionBank;

    public QuestionBank(String str) {
        questionBank = new ArrayList<>();
        System.out.println("Loading... (may take up to 20 seconds)\n");
        mkQuestionsFromString(str);
    }

    /** returns number of questions in bank **/
    public int numOfQuestions() { return questionBank.size(); }

    /** returns the answer to the question at the given index **/
    public String getAnswer(int i) {
        return questionBank.get(i).getCorrectAnswer();
    }

    /** prints all questions in the bank **/
    public void printBank(boolean showAnswers) {
        int i=1;
        for (Question question : questionBank) {
            System.out.print(i + ": ");
            question.printQuestion(showAnswers);
            System.out.print("\n");
            i++;
        }
    }

    /** populates question bank with questions created from a mixes string **/
    public void mkQuestionsFromString(String str) {
        String[] brokenDownStr = breakDownStr(str.replaceAll(" ", ""));

        for (String seq : brokenDownStr) {
            if (Character.isDigit(seq.charAt(0))) {
                for (Question q : NumberQuestionMaker.mkQsFromGroup(seq)) {
                    questionBank.add(q);
                }
            }

            if (Character.isLetter(seq.charAt(0))) {
                for (Question q : LetterQuestionsMaker.mkQsFromGroup(seq)) {
                    questionBank.add(q);
                }
            }
        }
    }

    /** converts a string into an array with elements grouped in letters and digits **/
    private String[] breakDownStr(String str) {
        String output = "";
        String divChar = "-";

        for (int c = 1; c < str.length(); c++) {
            output += str.charAt(c - 1);
            if (Character.isDigit(str.charAt(c - 1)) && (Character.isLetter(str.charAt(c)))) {
                output += divChar;
            }
            if (Character.isLetter(str.charAt(c - 1)) && (Character.isDigit(str.charAt(c)))) {
                output += divChar;
            }
        }
        output += str.charAt(str.length() - 1);
        return output.split(divChar);
    }
}
