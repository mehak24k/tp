package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ANSWER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEFINITION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TITLE;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.FlashcardBook;
import seedu.address.model.Model;
import seedu.address.model.QuizBook;
import seedu.address.model.person.Flashcard;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.quiz.Response;
import seedu.address.testutil.EditFlashcardDescriptorBuilder;

/**
 * Contains helper methods for testing commands.
 */
public class CommandTestUtil {

    public static final String VALID_NAME_BUBBLE_SORT = "bubble sort"; //different flashcard name from Bubble Sort
    public static final String VALID_NAME_HEAPING = "heaping";
    public static final String VALID_DEFINITION_BUBBLE_SORT = "<definition placeholder 1>";
    public static final String VALID_DEFINITION_HEAPING = "<definition placeholder 2>";
    public static final String VALID_TAG_DIFFICULT = "difficult";
    public static final String VALID_TAG_FINAL = "final";
    public static final String NAME_DESC_BUBBLE_SORT = " " + PREFIX_TITLE + VALID_NAME_BUBBLE_SORT;
    public static final String NAME_DESC_HEAPING = " " + PREFIX_TITLE + VALID_NAME_HEAPING;
    public static final String DEFINITION_DESC_BUBBLE_SORT = " " + PREFIX_DEFINITION + VALID_DEFINITION_BUBBLE_SORT;
    public static final String DEFINITION_DESC_HEAPING = " " + PREFIX_DEFINITION + VALID_DEFINITION_HEAPING;
    public static final String TAG_DESC_FINAL = " " + PREFIX_TAG + VALID_TAG_FINAL;
    public static final String TAG_DESC_DIFFICULT = " " + PREFIX_TAG + VALID_TAG_DIFFICULT;
    public static final String INVALID_NAME_DESC = " " + PREFIX_TITLE + "Bubble&Sort"; // '&' not allowed in names
    public static final String INVALID_DEFINITION_DESC = " "
            + PREFIX_DEFINITION; // empty string not allowed for addresses
    public static final String INVALID_TAG_DESC = " " + PREFIX_TAG + "hubby*"; // '*' not allowed in tags

    public static final String VALID_TF_ANSWER_TRUE = "true";
    public static final String VALID_TF_ANSWER_FALSE = "false";
    public static final String VALID_MCQ_ANSWER_OPTION_ONE = "1";
    public static final String TF_ANSWER_DESC_TRUE = " " + PREFIX_ANSWER + VALID_TF_ANSWER_TRUE;
    public static final String TF_ANSWER_DESC_FALSE = " " + PREFIX_ANSWER + VALID_TF_ANSWER_FALSE;
    public static final String MCQ_ANSWER_DESC_ONE = " " + PREFIX_ANSWER + VALID_MCQ_ANSWER_OPTION_ONE;
    public static final String INVALID_ANSWER_DESC = " " + PREFIX_ANSWER; //empty string not allowed for answers

    public static final String PREAMBLE_WHITESPACE = "\t  \r  \n";
    public static final String PREAMBLE_NON_EMPTY = "NonEmptyPreamble";

    public static final EditCommand.EditFlashcardDescriptor DESC_BUBBLE_SORT;
    public static final EditCommand.EditFlashcardDescriptor DESC_HEAPING;

    static {
        DESC_BUBBLE_SORT = new EditFlashcardDescriptorBuilder().withName(VALID_NAME_BUBBLE_SORT)
                .withDefinition(VALID_DEFINITION_BUBBLE_SORT)
                .withTags(VALID_TAG_FINAL).build();
        DESC_HEAPING = new EditFlashcardDescriptorBuilder().withName(VALID_NAME_HEAPING)
                .withDefinition(VALID_DEFINITION_HEAPING)
                .withTags(VALID_TAG_DIFFICULT, VALID_TAG_FINAL).build();
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - the returned {@link CommandResult} matches {@code expectedCommandResult} <br>
     * - the {@code actualModel} matches {@code expectedModel}
     */
    public static void assertCommandSuccess(Command command, Model actualModel, CommandResult expectedCommandResult,
            Model expectedModel) {
        try {
            CommandResult result = command.execute(actualModel);
            assertEquals(expectedCommandResult, result);
            assertEquals(expectedModel, actualModel);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    /**
     * Convenience wrapper to {@link #assertCommandSuccess(Command, Model, CommandResult, Model)}
     * that takes a string {@code expectedMessage}.
     */
    public static void assertCommandSuccess(Command command, Model actualModel, String expectedMessage,
            Model expectedModel) {
        CommandResult expectedCommandResult = new CommandResult(expectedMessage);
        assertCommandSuccess(command, actualModel, expectedCommandResult, expectedModel);
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - the returned {@link CommandResult} matches {@code expectedCommandResult} <br>
     * - the {@code actualModel} matches {@code expectedModel}
     */
    public static void assertCommandSuccessQuiz(Command command, Model actualModel, boolean isQuizMode,
                                                boolean hasCurrentAttempt, CommandResult expectedCommandResult,
                                                Model expectedModel) {
        try {
            if (isQuizMode) {
                actualModel.flipQuizMode();
            }
            if (hasCurrentAttempt) {
                actualModel.startAttempt();
            }
            CommandResult result = command.execute(actualModel);
            assertEquals(expectedCommandResult, result);
            assertEquals(expectedModel, actualModel);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    /**
     * Convenience wrapper to {@link #assertCommandSuccessQuiz)}
     * that takes a string {@code expectedMessage}.
     */
    public static void assertCommandSuccessQuiz(Command command, Model actualModel, boolean isQuizMode,
                                                boolean hasCurrentAttempt, String expectedMessage,
                                                Model expectedModel) {
        CommandResult expectedCommandResult = new CommandResult(expectedMessage);
        assertCommandSuccessQuiz(command, actualModel, isQuizMode, hasCurrentAttempt, expectedCommandResult, expectedModel);
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - a {@code CommandException} is thrown <br>
     * - the CommandException message matches {@code expectedMessage} <br>
     * - the address book, filtered flashcard list and selected flashcard in {@code actualModel} remain unchanged
     */
    public static void assertCommandFailure(Command command, Model actualModel, String expectedMessage) {
        // we are unable to defensively copy the model for comparison later, so we can
        // only do so by copying its components.
        FlashcardBook expectedFlashcardBook = new FlashcardBook(actualModel.getFlashcardBook());
        List<Flashcard> expectedFilteredList = new ArrayList<>(actualModel.getFilteredFlashcardList());

        assertThrows(CommandException.class, expectedMessage, () -> command.execute(actualModel));
        assertEquals(expectedFlashcardBook, actualModel.getFlashcardBook());
        assertEquals(expectedFilteredList, actualModel.getFilteredFlashcardList());
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - a {@code CommandException} is thrown <br>
     * - the CommandException message matches {@code expectedMessage} <br>
     * - the address book, filtered flashcard list and selected flashcard in {@code actualModel} remain unchanged
     */
    public static void assertCommandFailureQuiz(Command command, Model actualModel, String expectedMessage) {
        // we are unable to defensively copy the model for comparison later, so we can
        // only do so by copying its components.
        QuizBook expectedQuizBook = new QuizBook(actualModel.getQuizBook());
        List<Response> expectedFilteredList = new ArrayList<>(actualModel.getResponseList());

        assertThrows(CommandException.class, expectedMessage, () -> command.execute(actualModel));
        assertEquals(expectedQuizBook, actualModel.getQuizBook());
        assertEquals(expectedFilteredList, actualModel.getResponseList());
    }

    /**
     * Updates {@code model}'s filtered list to show only the flashcard at the given {@code targetIndex} in the
     * {@code model}'s address book.
     */
    public static void showPersonAtIndex(Model model, Index targetIndex) {
        assertTrue(targetIndex.getZeroBased() < model.getFilteredFlashcardList().size());
        Flashcard flashcard = model.getFilteredFlashcardList().get(targetIndex.getZeroBased());
        final String[] splitName = flashcard.getTitle().fullTitle.split("\\s+");
        model.updateFilteredFlashcardList(new NameContainsKeywordsPredicate(splitName[0]));

        assertEquals(1, model.getFilteredFlashcardList().size());
    }

}
