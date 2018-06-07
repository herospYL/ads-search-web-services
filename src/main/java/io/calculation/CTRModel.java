package io.calculation;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.extra.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CTRModel {
    private ArrayList<Double> weights;
    private Double bias;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CTRModel(Regions regions, String bucket, String method) {
        weights = new ArrayList<>();
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(regions).withCredentials(new ProfileCredentialsProvider()).build();

        S3Object s3Object = s3Client.getObject(bucket, method);

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
