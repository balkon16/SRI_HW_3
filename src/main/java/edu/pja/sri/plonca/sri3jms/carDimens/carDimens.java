package edu.pja.sri.plonca.sri3jms.carDimens;

import java.util.Map;
import static java.util.Map.entry;

// TODO: dopisać w engineLimits.json granice dla pozostałych wymiarów (np. oilPressure).
// TODO: dopisać w engineStatusValues.json przykładowe wartości i prawdopodobieństwa dla pozostałych wymiarów (np. oilPressure).

public class carDimens {
    public static final String UPPER_LIMIT_NAME = "upper";
    public static final String LOWER_LIMIT_NAME = "lower";
    public static final String LIMITS_FILE_PATH = "src/main/java/edu/pja/sri/plonca/sri3jms/carDimens/engineLimits.json";
    public static final String ENGINE_STATUS_VALUES_FILE_PATH = "src/main/java/edu/pja/sri/plonca/sri3jms/carDimens/engineStatusValues.json";

    public static final Map<String, String> translationMap = Map.ofEntries(
            entry("engineTemperature", "temperatura silnika"),
            entry("oilPressure", "ciśnienie oleju"),
            entry("brakeFluidPressure", "ciśnienie płynu hamulcowego"),
            entry("rightFrontWheelPressure", "ciśnienie opony - prawa z przodu"),
            entry("leftFrontWheelPressure", "ciśnienie opony - lewa z przodu"),
            entry("rightRearWheelPressure", "ciśnienie opony - prawa z tyłu"),
            entry("leftRearWheelPressure", "ciśnienie opony - lewa z tyłu")
    );

}
