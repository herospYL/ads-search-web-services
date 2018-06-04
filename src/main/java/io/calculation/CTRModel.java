package io.calculation;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.extra.Utility;
import io.s3.S3Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CTRModel {
    private static CTRModel instance = null;
    private static ArrayList<Double> weights;
    private static Double bias;

    @Autowired
    private S3Property s3Property;

    @Autowired
    private CTRProperty ctrProperty;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CTRModel() {
        weights = new ArrayList<>();
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(s3Property.getRegions()).withCredentials(new ProfileCredentialsProvider()).build();

        S3Object s3Object = s3Client.getObject(s3Property.getBucket(), ctrProperty.getMethod());

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

    public static CTRModel getInstance() {
        if (instance == null) {
            instance = new CTRModel();
        }

        return instance;
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
