package ru.otus.hw.indicator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdminSecuredMethods {
    CREATE("insert"),
    UPDATE("update"),
    DELETE("deleteById");

    private final String methodName;

}
