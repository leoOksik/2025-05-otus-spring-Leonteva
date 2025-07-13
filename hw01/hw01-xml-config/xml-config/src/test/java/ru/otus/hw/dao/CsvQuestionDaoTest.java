package ru.otus.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.exceptions.QuestionReadException;

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

    @DisplayName("findAll() shouldn't throws QuestionReadException if CSV file is read correctly")
    @Test
    void findAllNotShouldThrowsQuestionReadExceptionWhenReadingValidCsvFile() {
        when(fileNameProvider.getTestFileName()).thenReturn("validQuestions.csv");
        assertDoesNotThrow(() -> questionDao.findAll());
    }

    @DisplayName("findAll() should throws QuestionReadException if CSV file is read incorrectly")
    @Test
    void findAllShouldThrowsQuestionReadExceptionWhenReadingInvalidCsvFile() {
        when(fileNameProvider.getTestFileName()).thenReturn("invalidQuestions.csv");
        assertThrows(QuestionReadException.class, () -> questionDao.findAll());
    }
}
