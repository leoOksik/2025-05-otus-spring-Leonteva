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
            question.answers().forEach(answer -> ioService.printFormattedLine(answer.text()));
            ioService.printLine("");
            String userAnswer = ioService.readString();
            var isAnswerValid = question.answers().stream()
                    .anyMatch(answer -> answer.text().equalsIgnoreCase(userAnswer) && answer.isCorrect());
            testResult.applyAnswer(question, isAnswerValid);
            ioService.printLine("");
        }
        return testResult;
    }
}
