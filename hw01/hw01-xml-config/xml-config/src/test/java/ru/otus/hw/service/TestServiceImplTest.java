package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.anyString;


class TestServiceImplTest {

    private IOService ioService;
    private QuestionDao questionDao;
    private Question question;
    private Answer answer;
    private TestService testService;

    @BeforeEach
    void setUp() {
        ioService = mock(IOService.class);
        questionDao = mock(QuestionDao.class);
        testService = new TestServiceImpl(ioService, questionDao);
        question = mock(Question.class);
        answer = mock(Answer.class);
    }

    @DisplayName("Method executeTest() should call IOService methods the expected number of times")
    @Test
    void shouldCallIoServiceMethodsExpectedNumberOfTimes() {

        List<Question> questions = Collections.nCopies(5, question);
        List<Answer> answerList = Collections.nCopies(4, answer);

        when(answer.text()).thenReturn("");
        when(question.text()).thenReturn("");
        when(question.answers()).thenReturn(answerList);
        when(questionDao.findAll()).thenReturn(questions);

        testService.executeTest();

        verify(ioService, times(1)).printFormattedLine("Please answer the questions below%n");
        verify(ioService, times(26)).printLine(anyString()); // включая пустую строку
    }
}
