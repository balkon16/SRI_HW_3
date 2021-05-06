package edu.pja.sri.plonca.sri3jms.receiver;

import com.jayway.jsonpath.JsonPath;
import edu.pja.sri.plonca.sri3jms.carDimens.carDimens;
import edu.pja.sri.plonca.sri3jms.config.JmsConfig;
import edu.pja.sri.plonca.sri3jms.helpers.JsonReader;
import edu.pja.sri.plonca.sri3jms.model.AnalyzerMessage;
import edu.pja.sri.plonca.sri3jms.model.CarStatusMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CarEngineStatusAnalyzer {

    private final JmsTemplate jmsTemplate;

    public void sendMessage(String messageContent) {
        AnalyzerMessage message = AnalyzerMessage.builder()
                .id(AnalyzerMessage.nextId())
                .createdAt(LocalDateTime.now())
                .message("Sent from CarEngineStatusAnalyzer: " + messageContent)
                .build();
        jmsTemplate.convertAndSend(JmsConfig.QUEUE_COCKPIT_ANALYZER, message);

    }

    @SneakyThrows
    private Map<String, ArrayList<String>> analyzeMessage(CarStatusMessage message) {
        // TODO: dodać logikę, która będzie zwracała wartość inną niż null, gdy pojawi się ostrzeżenie lub zagrożenie.
        //  dla każdego wymiaru określić czy jest ostrzeżenie czy zagrożenie

        Object engineStatusValuesMap = JsonReader.readJsonFile(carDimens.LIMITS_FILE_PATH);
        ArrayList<String> warningsArray = new ArrayList<>();
        ArrayList<String> errorsArray = new ArrayList<>();
        Map<String, Double> statusMap = message.getStatusMap();
        Iterator<Map.Entry<String, Double>> it = statusMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Double> pair = it.next();
            String dimenName = pair.getKey();
            Double dimenValue = pair.getValue();
            //    ArrayList<Double> testList = JsonPath.read(document, "$.engineTemperature.upper");
            ArrayList<Double> upperBounds = JsonPath.read(engineStatusValuesMap, String.format("$.%s.%s", dimenName, carDimens.UPPER_LIMIT_NAME));
            ArrayList<Double> lowerBounds = JsonPath.read(engineStatusValuesMap, String.format("$.%s.%s", dimenName, carDimens.LOWER_LIMIT_NAME));
            int dangerLevel = assessDangerLevel(dimenValue, upperBounds, lowerBounds);
            if (dangerLevel == 1)
                errorsArray.add(String.format("Danger level reached: %s at %f", dimenName, dimenValue));
            if (dangerLevel == -1)
                warningsArray.add(String.format("Warning level reached: %s at %f", dimenName, dimenValue));
        }

        Map<String, ArrayList<String>> warningsErrorsMap = new HashMap<>();
        warningsErrorsMap.put("warnings", warningsArray);
        warningsErrorsMap.put("errors", errorsArray);
        return warningsErrorsMap;
    }

    private int assessDangerLevel(Double value, ArrayList<Double> upperBounds, ArrayList<Double> lowerBounds) {
        // -1 -> warning; 0 -> normal; 1 -> error
        if (value > upperBounds.get(0)) return 1;
        if (value > upperBounds.get(1)) return -1;
        if (value < lowerBounds.get(0)) return 1;
        if (value < lowerBounds.get(1)) return -1;
        return 0;
    }

    @JmsListener(destination = JmsConfig.TOPIC_CAR_ENGINE_STATUS, containerFactory =
            "topicConnectionFactory")
    public void receiveMessage(@Payload CarStatusMessage convertedMessage,
                               @Headers MessageHeaders messageHeaders,
                               Message message) {
//        System.out.println("Analyzer received a message: " + convertedMessage);
        Map<String, ArrayList<String>> analysisResult = analyzeMessage(convertedMessage);
        System.out.println("Analyzed message: " + analysisResult);
        // TODO: metoda wywołana tylko, gdy analyzeMessage nie zwróci nulla.
//        sendMessage(String.valueOf(int_random));
    }

}
