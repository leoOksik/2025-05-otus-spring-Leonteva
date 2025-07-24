package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;
import ru.otus.hw.exceptions.ResourceNotFoundException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        String fileName = fileNameProvider.testFileName();
        List<QuestionDto> questionDtoList;

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
             BufferedReader reader = inputStream != null ?
                     new BufferedReader(new InputStreamReader(inputStream)) : null) {

            if (reader == null) {
                throw new ResourceNotFoundException("Resource not found: " + fileName);
            }

            questionDtoList = new CsvToBeanBuilder<QuestionDto>(reader)
                    .withType(QuestionDto.class).withSeparator(';')
                    .withSkipLines(1).withIgnoreLeadingWhiteSpace(true).build().parse();
        } catch (IOException | RuntimeException ex) {
            throw new QuestionReadException("Can't read csv file: " + fileName, ex);
        }
        return questionDtoList.stream().map(QuestionDto::toDomainObject).toList();
    }
}
