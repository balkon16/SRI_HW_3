package edu.pja.sri.plonca.sri3jms.receiver;

import edu.pja.sri.plonca.sri3jms.config.JmsConfig;
import edu.pja.sri.plonca.sri3jms.model.GeneralMessage;
import edu.pja.sri.plonca.sri3jms.model.PitStopRequestMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import java.time.LocalDateTime;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class TeamDesktopReceiver {
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.QUEUE_TEAM_DESKTOP, containerFactory =
            "queueConnectionFactory")
    public void receiveMessage(@Payload GeneralMessage convertedMessage,
                                       @Headers MessageHeaders messageHeaders,
                                       Message message) {
        System.out.println("TeamDesktopReceiver.receiveMessage, message: " + convertedMessage);
    }

    @JmsListener(destination = JmsConfig.QUEUE_PITSTOP_REQUEST)
    public void receiveAndRespond(@Payload PitStopRequestMessage convertedMessage,
                                  @Headers MessageHeaders headers,
                                  Message message) throws JMSException {
        System.out.println("TeamDesktopReceiver.receiveAndRespond message: " + convertedMessage);
        Destination replyTo = message.getJMSReplyTo();
        String teamResponse =  (new Random().nextDouble() < 0.5) ? "Accepted" : "Declined";
        GeneralMessage msg = GeneralMessage.builder()
                .id(GeneralMessage.nextId())
                .createdAt(LocalDateTime.now())
                .message(teamResponse)
                .build();
        jmsTemplate.convertAndSend(replyTo, msg);
    }
}
