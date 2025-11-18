package ch.devprojects.orderflow.testing;

import java.lang.annotation.*;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Replacement for the deprecated @MockBean.
 *
 * Usage:
 *   @MockService(MyService.class)
 *   private MyService myService;
 *
 * This will:
 *   - create a Mockito mock of the given class
 *   - inject it into the test context
 *   - avoid all @MockBean warnings
 *
 * Works for all @WebMvcTest classes.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(MockService.Config.class)
public @interface MockService {

    Class<?> value();

    @TestConfiguration
    class Config {

        @Bean
        public Object mockService(MockService annotation) {
            return Mockito.mock(annotation.value());
        }
    }
}