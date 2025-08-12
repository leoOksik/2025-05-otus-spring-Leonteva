package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.mock;

@SpringBootTest(classes = TestServiceImplTest.TestConfig.class)
class TestServiceImplTest {

    @MockitoBean
    private LocalizedIOService ioService;
    @MockitoBean
    private Question question;
    @MockitoBean
    private Answer answer;
    @MockitoBean
    private TestFileNameProvider testFileNameProvider;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private TestService testService;

    @Configuration
    static class TestConfig {
        @Bean
        public QuestionDao questionDao(TestFileNameProvider testFileNameProvider) {
            return new CsvQuestionDao(testFileNameProvider);
        }
        @Bean
        public TestService testService(LocalizedIOService ioService, QuestionDao questionDao) {
            return new TestServiceImpl(ioService, questionDao);
        }
    }

    @DisplayName("Method executeTestFor (Student student) should call IOService methods the expected number of times")
    @Test
    void shouldCallIoServiceMethodsExpectedNumberOfTimes() {

        QuestionDao questionDaoMock = mock(QuestionDao.class);
        testService = new TestServiceImpl(ioService, questionDaoMock);

        List<Question> questions = Collections.nCopies(5, question);
        List<Answer> answerList = Collections.nCopies(4, answer);

        when(answer.text()).thenReturn("");
        when(question.text()).thenReturn("");
        when(question.answers()).thenReturn(answerList);
        when(questionDaoMock.findAll()).thenReturn(questions);

        when(ioService.readIntForRangeWithPromptLocalized(anyInt(), anyInt(), anyString(), anyString())).thenReturn(1);

        testService.executeTestFor(new Student("FirstNameTest", "LastNameTest"));

        verify(ioService, times(6)).printLine(anyString());
        verify(ioService, times(25)).printFormattedLine(anyString());
    }

    @DisplayName("executeTestFor (Student student) should read questions from CSV and return test results")
    @Test
    void executeTestFor_shouldReadCsvAndReturnTestResults() {
        when(testFileNameProvider.getTestFileName()).thenReturn("validQuestions.csv");
        List<Question> questions = questionDao.findAll();
        List<Integer> rightAnswersCount = new ArrayList<>();

        for (Question question : questions) {
            for (int i = 0; i < question.answers().size(); i++) {
                if (question.answers().get(i).isCorrect()) {
                    rightAnswersCount.add(i + 1);
                }
            }
        }

        when(ioService.readIntForRangeWithPromptLocalized(anyInt(), anyInt(), anyString(), anyString()))
            .thenReturn(rightAnswersCount.get(0), rightAnswersCount.subList(1, rightAnswersCount.size()).toArray(new Integer[0]));

        Student student = new Student("FirstNameTest", "LastNameTest");
        TestResult result = testService.executeTestFor(student);

        assertAll("TestResult",
            () -> assertEquals(questions.size(), result.getAnsweredQuestions().size(), "All questions answered"),
            () -> assertEquals(questions.size(), result.getRightAnswersCount(), "all correct answers"),
            () -> assertEquals(student, result.getStudent(), "Student matched")
        );
    }
}
