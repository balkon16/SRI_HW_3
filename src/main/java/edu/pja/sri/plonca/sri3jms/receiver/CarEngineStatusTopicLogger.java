package edu.pja.sri.plonca.sri3jms.receiver;

import edu.pja.sri.plonca.sri3jms.carDimens.carDimens;
import edu.pja.sri.plonca.sri3jms.config.JmsConfig;
import edu.pja.sri.plonca.sri3jms.model.CarStatusMessage;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

@Component
public class CarEngineStatusTopicLogger {

    private String getFormattedMessage(CarStatusMessage message, Map<String, String> translationMap) {
        Locale plPLLocale = new Locale.Builder().setLanguage("pl").setRegion("PL").build();
        NumberFormat numberFormatter = NumberFormat.getNumberInstance(plPLLocale);
        Map<String, Double> measurementsMap = message.getStatusMap();

        StringBuilder stringBuilder = new StringBuilder("ID: " + message.getId() +
                ". Title: " + message.getTitle());
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("Generated at: " + message.getCreatedAt());
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
    public void receiveMessage(@Payload CarStatusMessage convertedMessage,
                               @Headers MessageHeaders messageHeaders,
                               Message message) {
        System.out.println(getFormattedMessage(convertedMessage, carDimens.translationMap));
    }
}
