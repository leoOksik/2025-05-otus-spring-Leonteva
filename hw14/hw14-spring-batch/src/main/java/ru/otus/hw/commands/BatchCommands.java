package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@RequiredArgsConstructor
@ShellComponent
public class BatchCommands {

    private final JobLauncher jobLauncher;

    private final Job jobMigration;

    @ShellMethod(value = "Migration from H2 to Mongo", key = "st_mg")
    public void checkMigrationJob() throws Exception {
        JobExecution execution = jobLauncher.run(
            jobMigration,
            new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters()
        );
        System.out.println(execution);
    }
}

