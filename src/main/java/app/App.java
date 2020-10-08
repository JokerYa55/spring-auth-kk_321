package app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * @author Vasiliy.Andricov
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"app"})
@Slf4j
public class App {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        log.info("Starting app ...");
        SpringApplication.run(App.class, args);
        log.info("Started app");
    }

}
