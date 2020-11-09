package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Flashcard;
import seedu.address.model.quiz.Attempt;
import seedu.address.model.quiz.Question;
import seedu.address.model.quiz.Response;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.PerformanceBook;


/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final FlashcardBook flashcardBook;
    private final ReadOnlyQuizBook readOnlyQuizBook = SampleDataUtil.getSampleQuizBook();
    private final QuizBook quizBook = new QuizBook(readOnlyQuizBook);
    private final PerformanceBook performanceBook;
    private final ObservableList<Question> filteredQuizList = this.quizBook.getQuestionList();
    private ObservableList<Response> responseList;
    private final ObservableList<Attempt> filteredAttemptList;
    private final UserPrefs userPrefs;
    private final FilteredList<Flashcard> filteredFlashcards;
    private boolean isQuizMode = false;
    private boolean hasCurrentAttempt = false;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyFlashcardBook flashcardBook, ReadOnlyUserPrefs userPrefs) {
        super();
        requireAllNonNull(flashcardBook, userPrefs);

        logger.fine("Initializing with address book: " + flashcardBook + " and user prefs " + userPrefs);

        this.flashcardBook = new FlashcardBook(flashcardBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredFlashcards = new FilteredList<>(this.flashcardBook.getFlashcardList());

        PerformanceBook tempPerformanceBook;

        try {
            tempPerformanceBook = new PerformanceBook();
        } catch (IOException e) {
            tempPerformanceBook = PerformanceBook.createDefaultPerformanceBook();
        }
        performanceBook = tempPerformanceBook;
        filteredAttemptList = this.performanceBook.getPerformance().getAttempts();
    }

    public ModelManager() {
        this(new FlashcardBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getFlashcardBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public void setFlashcardBookFilePath(Path flashcardBookFilePath) {
        requireNonNull(flashcardBookFilePath);
        userPrefs.setAddressBookFilePath(flashcardBookFilePath);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setFlashcardBook(ReadOnlyFlashcardBook flashcardBook) {
        this.flashcardBook.resetData(flashcardBook);
    }

    @Override
    public ReadOnlyFlashcardBook getFlashcardBook() {
        return flashcardBook;
    }

    @Override
    public boolean hasFlashcard(Flashcard flashcard) {
        requireNonNull(flashcard);
        return flashcardBook.hasFlashcard(flashcard);
    }

    @Override
    public void deleteFlashcard(Flashcard target) {
        flashcardBook.removeFlashcard(target);
    }

    @Override
    public void flipFlashcard(Flashcard target) {
        flashcardBook.flipFlashcard(target);
    }

    @Override
    public void addFlashcard(Flashcard flashcard) {
        flashcardBook.addFlashcard(flashcard);
        updateFilteredFlashcardList(PREDICATE_SHOW_ALL_FLASHCARD);
    }

    @Override
    public void setFlashcard(Flashcard target, Flashcard editedFlashcard) {
        requireAllNonNull(target, editedFlashcard);
        flashcardBook.setFlashcard(target, editedFlashcard);
    }

    /**
     * Returns an unmodifiable view of the list of {@code Flashcard} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Flashcard> getFilteredFlashcardList() {
        return filteredFlashcards;
    }

    @Override
    public void updateFilteredFlashcardList(Predicate<Flashcard> predicate) {
        requireNonNull(predicate);
        filteredFlashcards.setPredicate(predicate);
    }

    @Override
    public void sortFilteredFlashcardList(String sortOrder) {
        flashcardBook.sortFlashcard(sortOrder);
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return flashcardBook.equals(other.flashcardBook)
                && userPrefs.equals(other.userPrefs)
                && filteredFlashcards.equals(other.filteredFlashcards)
                && isQuizMode == other.isQuizMode
                && hasCurrentAttempt == other.hasCurrentAttempt;
    }

    //=========== Filtered Question List Accessors =============================================================

    @Override
    public boolean getIsQuizMode() {
        return this.isQuizMode;
    }

    @Override
    public void flipQuizMode() {
        this.isQuizMode = !isQuizMode;
    }

    @Override
    public boolean hasCurrentAttempt() {
        return this.hasCurrentAttempt;
    }

    @Override
    public void startAttempt() {
        this.hasCurrentAttempt = true;
        quizBook.startAttempt();
    }

    @Override
    public boolean endAttempt() {
        this.hasCurrentAttempt = false;
        Attempt currentAttempt = quizBook.endAttempt();
        boolean isCurrentAttemptEmpty = currentAttempt.isEmpty();
        try {
            if (!isCurrentAttemptEmpty) {
                performanceBook.saveAttempt(currentAttempt);
            }
        } catch (IOException e) {
            logger.warning("Error here.");
        }
        return isCurrentAttemptEmpty;
    }
    @Override
    public void showAttempt(Attempt attempt) {
        requireNonNull(attempt);
        responseList = attempt.getResponses();
    }

    @Override
    public void recordResponse(Response response) {
        requireNonNull(response);
        quizBook.recordResponse(response);
    }

    @Override
    public void setSelectedIndex(Question question, String response) {
        requireAllNonNull(question, response);
        int index;
        if (question.isMcq()) {
            index = Integer.parseInt(response);
        } else {
            index = response.toLowerCase().equals("true") ? 1 : 2;
        }
        Question newQuestion = question.copy();
        newQuestion.setSelectedIndex(index);
        quizBook.setQuestion(question, newQuestion);
    }
    @Override
    public void setAllSelectedIndex(int index) {
        List<Question> newQuestions = new ArrayList<>();
        for (Question qn : quizBook.getQuestionList()) {
            Question newQn = qn.copy();
            newQn.setSelectedIndex(index);
            newQuestions.add(newQn);
        }
        quizBook.setQuestions(newQuestions);
    }

    public ObservableList<Question> getQuizList() {
        return this.filteredQuizList;
    }

    public ObservableList<Attempt> getAttemptList() {
        return this.filteredAttemptList;
    }

    public ObservableList<Response> getResponseList() {
        return this.responseList;
    }

    public QuizBook getQuizBook() {
        return this.quizBook;
    }

    /**
     * Saves performance in performance book.
     */
    public void savePerformance() throws IOException {
        performanceBook.savePerformance();
    }
}
