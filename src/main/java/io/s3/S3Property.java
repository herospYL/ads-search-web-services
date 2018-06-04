package io.s3;

import com.amazonaws.regions.Regions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("s3")
public class S3Property {
    private String bucket;
    private Regions regions;

    public String getBucket() {
        return bucket;
    }

    public Regions getRegions() {
        return regions;
    }
}
