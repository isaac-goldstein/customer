package com.acme.customer;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomerCreateListener implements MessageListener {

    private CustomerService customerService;
    private ObjectMapper objectMapper;

    public CustomerCreateListener(CustomerService customerService, ObjectMapper objectMapper) {
        this.customerService = customerService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message) {

        if (message instanceof TextMessage) {

            try {

                TextMessage textMessage = (TextMessage) message;
                String json = textMessage.getText();
                Customer customer = objectMapper.readValue(json, Customer.class);
                customerService.create(customer);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else {
            throw new IllegalArgumentException("Message must be of type TextMessage");
        }

    }

}
