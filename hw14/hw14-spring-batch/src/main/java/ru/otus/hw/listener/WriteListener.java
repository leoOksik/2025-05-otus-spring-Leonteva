package ru.otus.hw.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;
import ru.otus.hw.processor.Cache;

import java.util.Map;

@RequiredArgsConstructor
public class WriteListener<K, V> implements ItemWriteListener<V> {

    private final Cache cache;

    private final ProcessListener<K, V> processListener;

    @Override
    public void afterWrite(Chunk<? extends V> items) {
        Map<K, V> tempMap = processListener.getTempMap();
        try {
            for (Map.Entry<K, V> entry : tempMap.entrySet()) {
                K jpa = entry.getKey();
                V mongo = entry.getValue();

                Long postgresId = (Long) jpa.getClass().getMethod("getId").invoke(jpa);
                String mongoId = String.valueOf(mongo.getClass().getMethod("getId").invoke(mongo));

                String entityName = jpa.getClass().getSimpleName().replaceAll("\\$.*", "");

                cache.put(
                    entityName,
                    postgresId,
                    mongoId
                );
            }
        } catch (Exception ex) {
            throw new IllegalStateException("getId was failed", ex);
        } finally {
            tempMap.clear();
        }
    }
}
