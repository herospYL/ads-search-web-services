package io.calculation;

import java.util.List;

public interface CTRModel {
    double predictCTR(List<Double> features);
}
