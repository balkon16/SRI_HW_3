package edu.pja.sri.plonca.sri3jms.carDimens;

import java.util.Map;
import static java.util.Map.entry;

public class carDimens {
    public static final String UPPER_LIMIT_NAME = "upper";
    public static final String LOWER_LIMIT_NAME = "lower";
    public static final String LIMITS_FILE_PATH = "src/main/java/edu/pja/sri/plonca/sri3jms/carDimens/engineLimits.json";
    public static final String ENGINE_STATUS_VALUES_FILE_PATH = "src/main/java/edu/pja/sri/plonca/sri3jms/carDimens/engineStatusValues.json";

    public static final Map<String, String> translationMap = Map.ofEntries(
            entry("engineTemperature", "engine temperature"),
            entry("oilPressure", "oil pressure"),
            entry("brakeFluidPressure", "brake fluid pressure"),
            entry("rightFrontWheelPressure", "pressure: right front"),
            entry("leftFrontWheelPressure", "pressure: left front"),
            entry("rightRearWheelPressure", "pressure: right rear"),
            entry("leftRearWheelPressure", "pressure: left rear")
    );

}
