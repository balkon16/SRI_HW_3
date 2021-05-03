package edu.pja.sri.plonca.sri3jms.producer;

import edu.pja.sri.plonca.sri3jms.config.JmsConfig;
import edu.pja.sri.plonca.sri3jms.model.CarStatusMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wildfly.common.annotation.NotNull;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class CarEngineStatusTopicProducer {
    private final JmsTemplate jmsTemplate;

    @NotNull
    private Map<String, Double> generateEngineStatus(){
        // TODO: zaimplementować logikę różnych poziomów (bezpieczny, ostrzeżenie, zagrożenie) dla losowo wybranych
        //  elementów
        Map<String, Double> carMeasurementsMap = new HashMap<>();
        carMeasurementsMap.put("engineTemperature", 90.1);
        carMeasurementsMap.put("oilPressure", 45.12);
        carMeasurementsMap.put("brakeFluidPressure", 10.1);
        carMeasurementsMap.put("rightFrontWheelPressure", 32.5);
        carMeasurementsMap.put("leftFrontWheelPressure", 32.9);
        carMeasurementsMap.put("rightRearWheelPressure", 31.5);
        carMeasurementsMap.put("leftRearWheelPressure", 31.8);

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
