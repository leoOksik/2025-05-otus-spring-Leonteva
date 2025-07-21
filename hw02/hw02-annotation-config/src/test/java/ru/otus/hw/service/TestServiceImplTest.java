package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.mock;

class TestServiceImplTest {

    private IOService ioService;
    private QuestionDao questionDao;
    private Question question;
    private Answer answer;
    private TestService testService;
    private TestFileNameProvider testFileNameProvider;

    @BeforeEach
    void setUp() {
        ioService = mock(IOService.class);
        question = mock(Question.class);
        answer = mock(Answer.class);
        testFileNameProvider = mock(TestFileNameProvider.class);
        questionDao = new CsvQuestionDao(testFileNameProvider);
        testService = new TestServiceImpl(ioService, questionDao);
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

        testService.executeTestFor(new Student("FirstNameTest", "LastNameTest"));

        verify(ioService, times(26)).printFormattedLine(anyString());
        verify(ioService, times(11)).printLine(anyString());
    }

    @DisplayName("executeTestFor (Student student) should read questions from CSV and return test results")
    @Test
    void executeTestFor_shouldReadCsvAndReturnTestResults() {
        when(testFileNameProvider.testFileName()).thenReturn("validQuestions.csv");
        List<Question> questions = questionDao.findAll();

        List<String> correctAnswersList = questions.stream()
                .flatMap(q -> q.answers().stream().filter(Answer::isCorrect)
                        .findAny().map(Answer::text).stream()).toList();

        List<String> withIncorrectAnswersList = IntStream.range(0, correctAnswersList.size())
                .mapToObj(i -> i < 3 ? correctAnswersList.get(i) : "falseAnswers").toList();

        Iterator<String> iterator = withIncorrectAnswersList.iterator();
        when(ioService.readString()).thenAnswer(invocation -> iterator.next());

        Student student = new Student("FirstNameTest", "LastNameTest");
        TestResult result = testService.executeTestFor(student);

        assertAll("TestResult",
                () -> assertEquals(questions.size(), result.getAnsweredQuestions().size(), "All questions answered"),
                () -> assertEquals(3, result.getRightAnswersCount(), "3 correct answers"),
                () -> assertEquals(student, result.getStudent(), "Student matched")
        );
    }
}