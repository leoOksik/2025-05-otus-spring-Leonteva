package ru.otus.hw.indicator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import ru.otus.hw.services.BookServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class BookAdminAnnotationHealthCheckIndicatorSecurity implements HealthIndicator {

    @Override
    public Health health() {
        List<String> unSecuredAdminMethods = new ArrayList<>();

        for (var method : BookServiceImpl.class.getDeclaredMethods()) {
            boolean isMethodForAdmin = Arrays.stream(AdminSecuredMethods.values())
                .anyMatch(m -> method.getName().equals(m.getMethodName()));

            if (!isMethodForAdmin) {
                continue;
            }
            var preAuthorize = method.getAnnotation(PreAuthorize.class);

            if (preAuthorize == null ||
                !preAuthorize.value().replaceAll("\\s+", "").equals("hasRole('ADMIN')")) {
                unSecuredAdminMethods.add(method.getName());
            }
        }
        if (unSecuredAdminMethods.isEmpty()) {
            return Health.up()
                .withDetail("message", "Methods CREATE, UPDATE and DELETE have security protection").build();
        }
        return Health.down()
            .withDetail("message", "Errors -> Methods are not protected role ADMIN ( null or has any roles)")
            .withDetail("unsecuredMethods", unSecuredAdminMethods).build();
    }
}
