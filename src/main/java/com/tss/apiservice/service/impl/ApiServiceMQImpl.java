package com.tss.apiservice.service.impl;

import com.tss.apiservice.service.ApiServiceMQ;
import org.apache.activemq.command.ActiveMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Destination;

@Component
public class ApiServiceMQImpl implements ApiServiceMQ {

    private static Logger logger = LoggerFactory.getLogger(ApiServiceMQImpl.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    private static ActiveMQTopic dataEngineTopic = new ActiveMQTopic("DataEngine");

    @Override
    public void sendMessage(Destination destination , String message) {
        logger.info(" ApiService To MQ " + message);
        jmsTemplate.convertAndSend(destination, message);
    }

    @Override
    public void sendMessageToDataEngine( String message) {
        logger.info(" ApiService To MQ DataEngine" + message);
        jmsTemplate.convertAndSend(dataEngineTopic, message);
    }
}
