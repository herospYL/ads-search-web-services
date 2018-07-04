package io.service;

import java.io.IOException;

public interface AdsInitializationService {
    boolean initializeAds() throws IOException;
    boolean initializeFeature() throws IOException;
    boolean initializeSynonym() throws IOException;
    boolean initializeBudget() throws IOException;
}
