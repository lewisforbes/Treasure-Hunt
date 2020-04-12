public enum Command {
    QUESTIONS,
    ANSWER,
    UPDATE,
    POSTCODE,
    I_GIVE_UP,
    HELP;

    public static void explainCommands() {
        System.out.println("\nEnter your place once you think you've figured it out.\nA guess should match the place on Google Maps exactly, but are case insensitive.");
        System.out.println("\nThe following are commands that can be used: ");
        for (Command cmd : Command.values()) {
            System.out.println(Command.explainCommand(cmd));
        }
    }

    private static String explainCommand(Command cmd) {
        switch (cmd) {
            case QUESTIONS:
                return QUESTIONS.name() + " | displays all of the questions.";

            case ANSWER:
                return ANSWER.name() + " <question number> | displays the answer to a question.";

            case UPDATE:
                return UPDATE.name() + " <question number> <notes> | updates the notes for a question.";

            case POSTCODE:
                return POSTCODE.name() + " <your postcode> | checks if a postcode is correct.";

            case I_GIVE_UP:
                return I_GIVE_UP.name().replace("_", " ") + " | ends the game and displays the answers.";

            case HELP:
                return HELP + " | displays this menu.";
        }
        throw new IllegalArgumentException("An invalid command was passed to the explainCommand function");
    }
}
