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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class CarEngineStatusTopicProducer {
    private final JmsTemplate jmsTemplate;



    @SneakyThrows
    @NotNull
    private Map<String, Double> generateEngineStatus(){
        // TODO: zaimplementować logikę różnych poziomów (bezpieczny, ostrzeżenie, zagrożenie) dla losowo wybranych
        //  elementów
        Object engineStatusValuesMap = JsonReader.readJsonFile(carDimens.ENGINE_STATUS_VALUES_FILE_PATH);
        Double probValue = JsonPath.read(engineStatusValuesMap, "$.engineTemperature.prob");
        ArrayList<Double> dimenValues = JsonPath.read(engineStatusValuesMap, "$.engineTemperature.values");

        Map<String, Double> carMeasurementsMap = new HashMap<>();
        carMeasurementsMap.put("engineTemperature", engineGenerator.generateDimenValue(dimenValues, probValue));

//        carMeasurementsMap.put("", lowerBounds.get(0));
//        carMeasurementsMap.put("oilPressure", 45.12);
//        carMeasurementsMap.put("brakeFluidPressure", 10.1);
//        carMeasurementsMap.put("rightFrontWheelPressure", 32.5);
//        carMeasurementsMap.put("leftFrontWheelPressure", 32.9);
//        carMeasurementsMap.put("rightRearWheelPressure", 31.5);
//        carMeasurementsMap.put("leftRearWheelPressure", 31.8);

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
