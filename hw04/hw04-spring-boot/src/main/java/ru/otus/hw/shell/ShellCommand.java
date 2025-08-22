package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.service.LocalizedIOService;
import ru.otus.hw.service.TestRunnerService;

import java.util.Map;

@ShellComponent
@RequiredArgsConstructor
public class ShellCommand {
    private final TestRunnerService testRunnerService;
    private final AppProperties appProperties;
    private final LocalizedIOService ioService;

    @ShellMethod(value = "Change locale (ru/en). Example input: locale ru", key = "locale")
    public void changeLocale(String locale) {
        final Map<String, String> localeList = Map.of("ru", "ru-RU", "en", "en-US");

        String localeTag = localeList.get(locale.toLowerCase());
        if (localeTag == null) {
            throw new IllegalArgumentException("Locale must be en or ru");
        }
        appProperties.setLocale(localeTag);
    }

    @ShellMethod(value = "run test", key = "test")
    public void runTest() {
        testRunnerService.run();
    }
}
