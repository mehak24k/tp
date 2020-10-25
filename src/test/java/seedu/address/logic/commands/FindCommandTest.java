package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.commons.core.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalFlashcards.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Flashcard;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.PriorityContainsKeywordsPredicate;
import seedu.address.model.person.TagContainsKeywordsPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        NameContainsKeywordsPredicate firstPredicate =
                new NameContainsKeywordsPredicate("first");
        NameContainsKeywordsPredicate secondPredicate =
                new NameContainsKeywordsPredicate("second");

        FindCommand findFirstCommand = new FindCommand(Collections.singletonList(firstPredicate));
        FindCommand findSecondCommand = new FindCommand(Collections.singletonList(secondPredicate));

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(Collections.singletonList(firstPredicate));
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different flashcard -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_zeroKeywords_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(" ");
        FindCommand command = new FindCommand(Collections.singletonList(predicate));
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
    }

    //    @Test
    //    public void execute_multipleKeywords_multiplePersonsFound() {
    //        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
    //        List<Predicate<Flashcard>> predicates = Arrays.asList(preparePredicate("Kurz"),
    //                preparePredicate("Elle"), preparePredicate("Kunz"));
    //        FindCommand command = new FindCommand(predicates);
    //        expectedModel.updateFilteredPersonList(new ContainsAllKeywordsPredicate(predicates));
    //        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    //        assertEquals(Arrays.asList(CARL, ELLE, FIONA), model.getFilteredPersonList());
    //    }

    /**
     * Parses {@code userInput} into a {@code List<Predicate<Flashcard>>}.
     */
    @SafeVarargs
    private List<Predicate<Flashcard>> preparePredicateList(Predicate<Flashcard> ... predicates) {
        return Arrays.asList(predicates);
    }

    /**
     * Parses {@code userInput} into a {@code Predicate<Flashcard>}.
     */
    private Predicate<Flashcard> preparePredicate(String type, String userInput) {
        switch (type) {
        case "name":
            return new NameContainsKeywordsPredicate(userInput);
        case "tag":
            return new TagContainsKeywordsPredicate(userInput);
        case "priority":
            return new PriorityContainsKeywordsPredicate(userInput);
        default:
            return null;
        }
    }
}
