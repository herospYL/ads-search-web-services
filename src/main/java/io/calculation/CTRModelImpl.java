package io.calculation;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.extra.Utility;
import io.s3.InitializationProperty;
import io.s3.S3Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class CTRModelImpl implements CTRModel {
    private List<Double> weights;
    private double bias;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CTRModelImpl(S3Property s3Property, InitializationProperty initializationProperty) {
        weights = new ArrayList<>();
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(s3Property.getRegions()).withCredentials(new ProfileCredentialsProvider()).build();

        S3Object s3Object = s3Client.getObject(s3Property.getBucket(), initializationProperty.getCtrFile());

        try (BufferedReader ctrReader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()))) {
            String line;
            while ((line = ctrReader.readLine()) != null) {
                DocumentContext json = JsonPath.parse(line);
                weights = json.read("$.weights");
                bias = json.read("$.bias");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public double predictCTR(List<Double> features) {
        double pClick = bias;
        if (features.size() != weights.size()) {
            logger.error("ERROR: Size of features does not equal to weights");
            return pClick;
        }

        for (int i = 0; i < features.size(); i++) {
            pClick = pClick + weights.get(i) * features.get(i);
        }

        pClick = Utility.sigmoid(pClick);
        return pClick;
    }
}
