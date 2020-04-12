import java.util.ArrayList;

public class Question {
    private String question;
    private String correctAnswer;
    private String userNotes = null;
    private ArrayList<String> incorrectAnswers;

    /** number of incorrect answers to generate per question **/
    private int noOfIAs = 3;

    public Question(String question, String correctAnswer, ArrayList<String> incorrectAnswers) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;
    }

    public Question(String question, String correctAnswer, String[] incorrectAnswers) {
        this.question = question;
        this.correctAnswer = correctAnswer;

        ArrayList<String> temp = new ArrayList<>();
        for (String answer : incorrectAnswers) {
            temp.add(answer);
        }
        this.incorrectAnswers = temp;

    }

    public Question(String question, String correctAnswer) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = mkIncorrectAnswers(correctAnswer);
    }

    public void updateNotes(String notes) {
        userNotes = notes;
    }

    private ArrayList<String> mkIncorrectAnswers(String correctAnswer) {
        int correctAnswerInt;
        try {
            correctAnswerInt = Integer.parseInt(correctAnswer);
        } catch (NumberFormatException nfe) {
            return null;
        }

        ArrayList<String> output = new ArrayList<>();

        int max = 10;
        int min = -10;
        int toAdd;
        int range = max - min + 1;

        for (int i=1; i<=noOfIAs; i++) {
            while (true) {
                toAdd = (int) (Math.random() * range) + min;
                if ((toAdd != 0) && (!output.contains(toAdd)) && ((correctAnswerInt + toAdd) >= 1)) {
                    break;
                }
            }
            output.add("" + (correctAnswerInt + toAdd));
        }
        return output;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void printQuestion(boolean showAnswers) {
        System.out.println(question);
        if (userNotes == null) {
            System.out.println("Notes: [empty]");
        } else {
            System.out.println("Notes: " + userNotes);
        }

        if (showAnswers) {
            System.out.println("A: " + correctAnswer);
        }
    }
}
