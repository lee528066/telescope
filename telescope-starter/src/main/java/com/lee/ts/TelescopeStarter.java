package com.lee.ts;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;

/**
 * @author liwei
 */
@SpringBootApplication(scanBasePackages = {"com.lee.ts"})
public class TelescopeStarter {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TelescopeStarter.class);
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
        app.setBannerMode(Banner.Mode.CONSOLE);
        app.run(args);
    }
}
