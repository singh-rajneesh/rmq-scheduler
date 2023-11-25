package com.maverick.rmqscheduler.listener;

import com.maverick.rmqscheduler.constants.LiteralConstants;
import com.maverick.rmqscheduler.dto.MessageDto;
import com.maverick.rmqscheduler.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component(value = "ProcessingQueueListener")
@RequiredArgsConstructor
@Slf4j
public class ProcessingQueueListener {

    private final MessageService messageService;

    @RabbitListener(queues = "${messaging.processing.queue.name}", containerFactory = "simpleRabbitListenerContainerFactory")
    protected void listenMessage(
            MessageDto messageDto, Message message) {
        log.info("ProcessingQueueListener.listenMessage() :: consumed message={}", message);
        MessageProperties messageProperties = message.getMessageProperties();
        String correlationId = messageProperties.getCorrelationId();
        MDC.put(LiteralConstants.UUID, correlationId);
        messageService.processMessage(messageDto);
        messageService.deleteBackoffQueuesIfEmpty(messageDto);
    }
}
