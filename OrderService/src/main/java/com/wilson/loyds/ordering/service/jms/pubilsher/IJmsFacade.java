package com.wilson.loyds.ordering.service.jms.pubilsher;

public interface IJmsFacade {

    void sendSuccessfulMessage(String tenantId, Object object);
    void sendUnSuccessfulMessage(String tenantId, Object object);
}