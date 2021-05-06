package edu.pja.sri.plonca.sri3jms.producer;

import com.jayway.jsonpath.JsonPath;
import edu.pja.sri.plonca.sri3jms.carDimens.carDimens;
import edu.pja.sri.plonca.sri3jms.config.JmsConfig;
import edu.pja.sri.plonca.sri3jms.helpers.JsonReader;
import edu.pja.sri.plonca.sri3jms.helpers.engineGenerator;
import edu.pja.sri.plonca.sri3jms.model.CarStatusMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wildfly.common.annotation.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CarEngineStatusTopicProducer {
    private final JmsTemplate jmsTemplate;

    @SneakyThrows
    @NotNull
    private Map<String, Double> generateEngineStatus() {
        Object engineStatusValuesMap = JsonReader.readJsonFile(carDimens.ENGINE_STATUS_VALUES_FILE_PATH);
        Map<String, Double> carMeasurementsMap = new HashMap<>();
        for (String dimenName : carDimens.translationMap.keySet()) {
            Double probValue = JsonPath.read(engineStatusValuesMap, String.format("$.%s.prob", dimenName));
            ArrayList<Double> dimenValues = JsonPath.read(engineStatusValuesMap, String.format("$.%s.values", dimenName));
            carMeasurementsMap.put(dimenName, engineGenerator.generateDimenValue(dimenValues, probValue));
        }
        return carMeasurementsMap;
    }

    @Scheduled(fixedRate = 2000)
    public void sendCarEngineStatus() {

        Map<String, Double> engineStatusMeasurements = generateEngineStatus();

        CarStatusMessage message = CarStatusMessage.builder()
                .id(CarStatusMessage.nextId())
                .createdAt(LocalDateTime.now())
                .statusMap(engineStatusMeasurements)
                .title("Car engine status")
                .build();
        jmsTemplate.convertAndSend(JmsConfig.TOPIC_CAR_ENGINE_STATUS, message);
        System.out.println("CarEngineStatusTopicProducer.sendCarEngineStatus - sent message: " + message);

    }

}
