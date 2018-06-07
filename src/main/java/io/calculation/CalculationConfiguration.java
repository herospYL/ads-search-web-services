package io.calculation;

import io.s3.S3Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@ComponentScan("io")
@EnableAutoConfiguration
public class CalculationConfiguration {

    @Autowired
    private S3Property s3Property;

    @Autowired
    private CTRProperty CTRProperty;

    /**
     * @return The singleton bean for CTRModel
     */
    @Bean
    @Scope("singleton")
    public CTRModel CTRModel() {
        return new CTRModel(s3Property.getRegions(), s3Property.getBucket(), CTRProperty.getMethod());
    }
}
