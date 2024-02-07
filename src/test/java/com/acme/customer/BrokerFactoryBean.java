package com.acme.customer;

import java.net.URI;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;

public class BrokerFactoryBean implements FactoryBean<Broker> {

    @Override
    @Nullable
    public Broker getObject() throws Exception {
        BrokerService broker = new BrokerService();

        TransportConnector connector = new TransportConnector();
        connector.setUri(new URI("tcp://localhost:61616"));
        broker.addConnector(connector);

        broker.setPersistent(false);
        broker.start();
        return broker.getBroker();
    }

    @Override
    @Nullable
    public Class<?> getObjectType() {
        return null;
    }

}
