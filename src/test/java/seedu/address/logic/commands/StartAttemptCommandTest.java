package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccessQuiz;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalFlashcards.getTypicalFlashcardBook;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_FLASHCARD;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_FLASHCARD;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Flashcard;

public class StartAttemptCommandTest {
    private Model model = new ModelManager(getTypicalFlashcardBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() throws CommandException {
        StartAttemptCommand startAttemptCommand = new StartAttemptCommand();

        String expectedMessage = StartAttemptCommand.MESSAGE_ATTEMPT_ACKNOWLEDGEMENT;

        ModelManager expectedModel = new ModelManager(model.getFlashcardBook(), new UserPrefs());
        expectedModel.flipQuizMode();
        expectedModel.startAttempt();

        assertCommandSuccessQuiz(startAttemptCommand, model, true, false,
                expectedMessage, expectedModel);
    }
}
