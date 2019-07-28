package com.tss.apiservice.service;

import javax.jms.Destination;

public interface ApiServiceMQ {

    void sendMessage(Destination destination , String message);

    void sendMessageToDataEngine(String message);


}
