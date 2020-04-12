/** the keywords to trigger avilable commands **/
public enum Command {
    QUESTIONS,
    ANSWER,
    UPDATE,
    POSTCODE,
    I_GIVE_UP,
    HELP;

    /** the full commands for every keyword, in same order as Command enum **/
    private static final String[] fullCommands = new String[] {
            QUESTIONS.name(),
            ANSWER.name() + " <question number>",
            UPDATE.name() + " <question number> <notes>",
            POSTCODE.name() + " <your postcode>",
            I_GIVE_UP.name().replaceAll("_", " "),
            HELP.name()
    };


    /** the length all full commands should be (spaces are added to make up) **/
    private static int padding;

    /** prints out explanations for every command **/
    public static void explainCommands() {
        mkPadding();
        System.out.println("\nEnter your place once you think you've figured it out.\nA guess should match the place on Google Maps exactly, but are case insensitive.");
        System.out.println("\nThe following are commands that can be used: ");
        for (Command cmd : Command.values()) {
            System.out.println(Command.explainCommand(cmd));
        }
    }

    private static String explainCommand(Command cmd) {
        switch (cmd) {
            case QUESTIONS:
                return getPaddedCmd(fullCommands[getCommandIndex(QUESTIONS)]) + "| Displays all of the questions.";

            case ANSWER:
                return getPaddedCmd(fullCommands[getCommandIndex(ANSWER)]) + "| Displays the answer to a question.";

            case UPDATE:
                return getPaddedCmd(fullCommands[getCommandIndex(UPDATE)]) + "| Updates the notes for a question.";

            case POSTCODE:
                return getPaddedCmd(fullCommands[getCommandIndex(POSTCODE)]) + "| Checks if a postcode is correct.";

            case I_GIVE_UP:
                return getPaddedCmd(fullCommands[getCommandIndex(I_GIVE_UP)]) + "| Ends the game and displays the answers.";

            case HELP:
                return getPaddedCmd(fullCommands[getCommandIndex(HELP)]) + "| Displays this menu.";
        }
        throw new IllegalArgumentException("An invalid command was passed to the explainCommand function");
    }

    private static String getPaddedCmd(String fullCmd) {
        return fullCmd + " ".repeat(padding - fullCmd.length());
    }

    /** sets the padding int appropriately **/
    private static void mkPadding() {
        int maxCmdLength = fullCommands[0].length();

        for (String cmd : fullCommands) {
            if (cmd.length() > maxCmdLength) {
                maxCmdLength = cmd.length();
            }
        }

        padding = maxCmdLength + 1;
    }

    /** returns the index of the provided command in the Command enum **/
    private static int getCommandIndex(Command cmd) {
        Command[] cmdArray = Command.values();

        for (int i=0; i<cmdArray.length; i++) {
            if (cmdArray[i] == cmd)
                return i;
        }

        throw new IllegalArgumentException("Invalid command passed through.");
    }
}
