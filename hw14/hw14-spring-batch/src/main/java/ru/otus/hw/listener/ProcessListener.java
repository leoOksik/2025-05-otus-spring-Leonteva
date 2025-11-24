package ru.otus.hw.listener;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ItemProcessListener;


import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class ProcessListener<K, V> implements ItemProcessListener<K, V> {

    private final Map<K, V> tempMap = new HashMap<>();

    @Override
    public void afterProcess(K input, V output) {
        tempMap.put(input, output);
    }
}
