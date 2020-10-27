package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.commons.core.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalFlashcards.ALICE;
import static seedu.address.testutil.TypicalFlashcards.BENSON;
import static seedu.address.testutil.TypicalFlashcards.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.ContainsAllKeywordsPredicate;
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
        FindCommand findFirstCommand = new FindCommand(preparePredicateList(
                preparePredicate("name", "first")));
        FindCommand findSecondCommand = new FindCommand(preparePredicateList(
                preparePredicate("name", "second")));

        // same object -> returns true
        assertEquals(findFirstCommand, findFirstCommand);

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(preparePredicateList(
                preparePredicate("name", "first")));
        assertEquals(findFirstCommand, findFirstCommandCopy);

        // different types -> returns false
        assertNotEquals(1, findFirstCommand);

        // null -> returns false
        assertNotEquals(null, findFirstCommand);

        // different commands -> returns false
        assertNotEquals(findFirstCommand, findSecondCommand);
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

    @Test
    public void execute_oneKeyword_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 2);
        List<Predicate<Flashcard>> predicateList = preparePredicateList(
                preparePredicate("priority", "low"));
        FindCommand command = new FindCommand(predicateList);
        expectedModel.updateFilteredPersonList(new ContainsAllKeywordsPredicate(predicateList));
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(ALICE, BENSON), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleKeywords_onePersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        List<Predicate<Flashcard>> predicateList = preparePredicateList(
                preparePredicate("priority", "low"), preparePredicate("name", "Alice"));
        FindCommand command = new FindCommand(predicateList);
        expectedModel.updateFilteredPersonList(new ContainsAllKeywordsPredicate(predicateList));
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleKeywords_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        List<Predicate<Flashcard>> predicateList = preparePredicateList(
                preparePredicate("priority", "high"), preparePredicate("name", "Alice"));
        FindCommand command = new FindCommand(predicateList);
        expectedModel.updateFilteredPersonList(new ContainsAllKeywordsPredicate(predicateList));
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
    }

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
