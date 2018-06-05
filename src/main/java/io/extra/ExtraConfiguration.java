package io.extra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("io")
@EnableAutoConfiguration
public class ExtraConfiguration {

    @Autowired
    private ExtraProperty extraProperty;

    @Bean
    public Utility utility() {
        return new Utility(extraProperty.getStopWords());
    }

    @Bean
    public Filter filter() {
        return new Filter(extraProperty.getPClickThreshold(), extraProperty.getRelevanceScoreThreshold(), extraProperty.getMinNumOfAds());
    }
}
