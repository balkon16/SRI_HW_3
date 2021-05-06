package edu.pja.sri.plonca.sri3jms.receiver;

import edu.pja.sri.plonca.sri3jms.config.JmsConfig;
import edu.pja.sri.plonca.sri3jms.model.AnalyzerMessage;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.jms.Message;

@Component
public class TeamDesktopReceiver {
    @JmsListener(destination = JmsConfig.QUEUE_TEAM_DESKTOP, containerFactory =
            "queueConnectionFactory")
    public void receiveMessage(@Payload AnalyzerMessage convertedMessage,
                                       @Headers MessageHeaders messageHeaders,
                                       Message message) {
        System.out.println("TeamDesktopReceiver.receiveMessage, message: " + convertedMessage);
    }
}
