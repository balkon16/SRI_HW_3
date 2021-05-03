package edu.pja.sri.plonca.sri3jms.receiver;

import edu.pja.sri.plonca.sri3jms.config.JmsConfig;
import edu.pja.sri.plonca.sri3jms.model.CarStatusMessage;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.util.Map.entry;

@Component
public class CarEngineStatusTopicLogger {

    private final Map<String, String> translationMap = Map.ofEntries(
            entry("engineTemperature", "temperatura silnika"),
            entry("oilPressure", "ciśnienie oleju"),
            entry("brakeFluidPressure", "ciśnienie płynu hamulcowego"),
            entry("rightFrontWheelPressure", "ciśnienie opony - prawa z przodu"),
            entry("leftFrontWheelPressure", "ciśnienie opony - lewa z przodu"),
            entry("rightRearWheelPressure", "ciśnienie opony - prawa z tyłu"),
            entry("leftRearWheelPressure", "ciśnienie opony - lewa z tyłu")
    );

    private String getformattedMessage(CarStatusMessage message) {
        Locale plPLLocale = new Locale.Builder().setLanguage("pl").setRegion("PL").build();
        NumberFormat numberFormatter = NumberFormat.getNumberInstance(plPLLocale);
        Map<String, Double> measurementsMap = message.getStatusMap();

        StringBuilder stringBuilder = new StringBuilder("Identyfikator: " + message.getId() +
                ". Tytuł: " + message.getTitle());
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("Wygenerowana: " + message.getCreatedAt());
        stringBuilder.append(System.getProperty("line.separator"));

        for (Map.Entry<String, Double> measurementEntry: measurementsMap.entrySet()){
            String translatedName = translationMap.get(measurementEntry.getKey());
            Double measurementValue = measurementEntry.getValue();
            stringBuilder.append(translatedName + ": " + numberFormatter.format(measurementValue));
            stringBuilder.append(System.getProperty("line.separator"));
        }

        return stringBuilder.toString();
    }

    @JmsListener(destination = JmsConfig.TOPIC_CAR_ENGINE_STATUS, containerFactory =
            "topicConnectionFactory")
    public void receiveHelloMessage(@Payload CarStatusMessage convertedMessage,
                                    @Headers MessageHeaders messageHeaders,
                                    Message message) {
        LocalDateTime receivedAt = LocalDateTime.now();
        System.out.println(getformattedMessage(convertedMessage));
    }
}