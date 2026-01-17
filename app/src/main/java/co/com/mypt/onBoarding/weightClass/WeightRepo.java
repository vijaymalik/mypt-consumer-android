package co.com.mypt.onBoarding.weightClass;

import java.util.ArrayList;
import java.util.List;

public class WeightRepo {

    public boolean isKg = true;  // To track whether kg or lbs is selected

    // Method to get the labels for the selected unit (kg or lbs)
    public List<String> getWeightLabels() {
        List<String> labels = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            if (isKg) {
                labels.add(String.format("%d", i)); // Cast to double
                for (int decimal = 1; decimal <= 9; decimal++) {
                    double increment = i + decimal / 10.0;
                    labels.add(String.format("%.1f", increment)); // Smaller increments in kg
                }
            } else {
                double pounds = i * 2.20462;  // Convert kg to lbs
                labels.add(String.format("%.1f", pounds));
                for (int decimal = 1; decimal <= 9; decimal++) {
                    double increment = i + decimal / 10.0;
                    double lbsIncrement = increment * 2.20462; // Convert kg to lbs
                    labels.add(String.format("%.1f", lbsIncrement)); // Smaller increments in lbs
                }
            }
        }
        return labels;
    }

    public void setUnitToKg() {
        isKg = true;
    }

    public void setUnitToLbs() {
        isKg = false;
    }

    public int getStartWeight() {
        return 690;
    }
}
