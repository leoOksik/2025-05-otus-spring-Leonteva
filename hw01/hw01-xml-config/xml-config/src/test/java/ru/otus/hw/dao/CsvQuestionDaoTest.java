package ru.otus.hw.dao;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CsvQuestionDaoTest {

    private TestFileNameProvider fileNameProvider;
    private CsvQuestionDao questionDao;

    @BeforeEach
    void setUp() {
        fileNameProvider = mock(TestFileNameProvider.class);
        questionDao = new CsvQuestionDao(fileNameProvider);
    }

    @DisplayName("findAll() should read valid CSV without exception and parse data correctly")
    @Test
    void findAllShouldReadValidCsvAndParseDataCorrectly() {
        when(fileNameProvider.getTestFileName()).thenReturn("validQuestions.csv");
        List<Question> questions = assertDoesNotThrow(() -> questionDao.findAll());
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(questions).as("Questions must be 5").hasSize(5);

        questions.forEach(question -> {
            softly.assertThat(question.text())
                    .as("Question mustn't be blank")
                    .isNotBlank();

            softly.assertThat(question.answers())
                    .as("Answers mustn't  be null for question: '%s'", question.text())
                    .isNotNull();

            if (question.answers() != null) {
                softly.assertThat(question.answers())
                        .as("Answers must be 4").isNotNull().hasSize(4)
                        .allSatisfy(answer -> {
                            softly.assertThat(answer.text())
                                    .as("Answer mustn't be blank").isNotBlank();
                            softly.assertThat(answer.isCorrect())
                                    .as("isCorrect flag must be a boolean value").isInstanceOf(Boolean.class);
                        });

                softly.assertThat(question.answers().stream().anyMatch(Answer::isCorrect))
                        .as("Question must have more than zero correct answers").isTrue();
            }
        });

        softly.assertAll();
    }

    @DisplayName("findAll() should throws QuestionReadException if CSV file is read incorrectly")
    @Test
    void findAllShouldThrowsQuestionReadExceptionWhenReadingInvalidCsvFile() {
        when(fileNameProvider.getTestFileName()).thenReturn("invalidQuestions.csv");
        assertThrows(QuestionReadException.class, () -> questionDao.findAll());
    }
}
