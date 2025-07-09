package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;
import ru.otus.hw.exceptions.ResourceNotFoundException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {

   private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        String fileName = fileNameProvider.getTestFileName();
        List<Question> questionList = new ArrayList<>();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
                BufferedReader reader = inputStream != null ?
                        new BufferedReader(new InputStreamReader(inputStream)) : null) {

            if (reader == null) {
                throw new ResourceNotFoundException("Resource not found: " + fileName);
            }

            List<QuestionDto> questionDtoList = new CsvToBeanBuilder<QuestionDto>(reader)
                    .withType(QuestionDto.class).withSeparator(';')
                    .withSkipLines(1).withIgnoreLeadingWhiteSpace(true).build().parse();

            for (QuestionDto questionDto : questionDtoList) {
                questionList.add(questionDto.toDomainObject());
            }

        } catch (IOException ex) {
            throw new QuestionReadException("Can't read csv file: " + fileName, ex);
        }
        return new ArrayList<>(questionList);
    }
}
