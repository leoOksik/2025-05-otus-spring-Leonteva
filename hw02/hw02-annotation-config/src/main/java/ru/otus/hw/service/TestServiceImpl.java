package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below in assembly language%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question : questions) {
            ioService.printFormattedLine(question.text());
            int maxAnswerCount = question.answers().size();
            int minAnswerCount = 1;

            if (maxAnswerCount == 0) {
                throw new IllegalStateException("Answers is empty for question: " + question.text());
            }

            question.answers().forEach(answer -> ioService.printFormattedLine(answer.text()));
            int userAnswer = ioService.readIntForRangeWithPrompt(minAnswerCount, maxAnswerCount,
                "Choose the correct answer from  %d to %d".formatted(minAnswerCount, maxAnswerCount),
                "Incorrect input. Enter a number between %d and %d".formatted(minAnswerCount, maxAnswerCount));
            var isAnswerValid = question.answers().get(userAnswer - 1).isCorrect();
            testResult.applyAnswer(question, isAnswerValid);
            ioService.printLine("");
        }
        return testResult;
    }
}
