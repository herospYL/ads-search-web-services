package io.repository;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan("io")
@EnableJpaRepositories(basePackages = "io.repository")
public class AdSearchConfiguration {
}
