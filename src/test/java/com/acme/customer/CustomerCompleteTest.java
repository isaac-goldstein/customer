package com.acme.customer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;

@WebAppConfiguration
public class CustomerCompleteTest extends AbstractTest {

        @Autowired
        private CustomerDao dao;

        @Autowired
        private CustomerService service;

        @Autowired
        private JmsTemplate jmsTemplate;

        @Autowired
        private CustomerService customerService;

        @Autowired
        private TransactionTemplate transactionTemplate;

        @Autowired
        private WebApplicationContext wac;

        private MockMvc mockMvc;

        @Before
        public void setup() {
                this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        }

        @Test
        public void testGetExistingCustomer() throws Exception {
                mockMvc.perform(get("/customer/{id}", 1))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value("1"));
        }

        @Test
        public void testGetNonExistingCustomer() throws Exception {
                mockMvc.perform(get("/customer/{id}", 999))
                                .andExpect(status().isNotFound());
        }

        @Test

        public void testCreateAndDeleteCustomer() throws Exception {
                mockMvc.perform(post("/customer")
                                .param("id", "2")
                                .param("firstName", "John")
                                .param("lastName", "Doe"))
                                .andExpect(status().isCreated());

                mockMvc.perform(get("/customer/{id}", 2))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value("2"));

                mockMvc.perform(delete("/customer/{id}", 2))
                                .andExpect(status().isOk());

                mockMvc.perform(get("/customer/{id}", 2))
                                .andExpect(status().isNotFound());
        }

        @Test
        public void testDeleteNonExistingCustomer() throws Exception {
                mockMvc.perform(delete("/customer/{id}", 999))
                                .andExpect(status().isNotFound());
        }

        @Test
        public void testDaoReadExistingCustomer() {
                // Normal condition: Reading an existing customer
                Customer customer = dao.read(1);
                assertNotNull(customer);
        }

        @Test
        public void testDaoCreateAndDeleteCustomer() {
                // Normal condition: Creating and deleting a customer
                Customer newCustomer = new Customer();
                newCustomer.setId(2);
                newCustomer.setFirstName("John");
                newCustomer.setLastName("Doe");
                assertEquals(1, dao.create(newCustomer));

                Customer createdCustomer = dao.read(2);
                assertNotNull(createdCustomer);
                assertEquals("John", createdCustomer.getFirstName());
                assertEquals("Doe", createdCustomer.getLastName());

                dao.delete(2);
                assertNull(dao.read(2));
        }

        @Test
        public void testDaoReadNonExistingCustomer() {
                // Edge condition: Reading a non-existing customer
                assertNull(dao.read(999));
        }

        @Test
        public void testDaoDeleteNonExistingCustomer() {
                // Negative condition: Deleting a non-existing customer
                assertEquals(0, dao.delete(999));
        }

        @Test
        public void testServiceReadExistingCustomer() {
                // Normal condition: Reading an existing customer
                Customer customer = service.read(1);
                assertNotNull(customer);
        }

        @Test
        public void testServiceCreateAndDeleteCustomer() {
                // Normal condition: Creating and deleting a customer
                Customer newCustomer = new Customer();
                newCustomer.setId(2);
                newCustomer.setFirstName("John");
                newCustomer.setLastName("Doe");
                assertEquals(1, service.create(newCustomer));

                Customer createdCustomer = service.read(2);
                assertNotNull(createdCustomer);
                assertEquals("John", createdCustomer.getFirstName());
                assertEquals("Doe", createdCustomer.getLastName());

                service.delete(2);
                assertNull(service.read(2));
        }

        @Test
        public void testServiceReadNonExistingCustomer() {
                // Edge condition: Reading a non-existing customer
                assertNull(service.read(999));
        }

        @Test
        public void testServiceDeleteNonExistingCustomer() {
                // Negative condition: Deleting a non-existing customer
                assertEquals(0, service.delete(999));
        }

        @Test
        public void testNormalCondition() throws InterruptedException {
                String msg = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"id\":2}";

                transactionTemplate.executeWithoutResult(status -> {
                        jmsTemplate.send("customer.create", session -> session.createTextMessage(msg));
                });

                // Allow time for the message to be processed
                Thread.sleep(2000);

                Customer customer = customerService.read(2);
                Assert.assertNotNull(customer);
                Assert.assertEquals("John", customer.getFirstName());
                Assert.assertEquals("Doe", customer.getLastName());

                // cleanup
                transactionTemplate.executeWithoutResult(status -> {
                        customerService.delete(2);
                });
        }

        @Test
        public void testEdgeCondition() throws InterruptedException {
                // Edge condition: Creating a customer with an ID that already exists
                String msg = "{\"firstName\":\"Jane\",\"lastName\":\"Smith\",\"id\":1}";

                transactionTemplate.executeWithoutResult(status -> {
                        jmsTemplate.send("customer.create", session -> session.createTextMessage(msg));
                });

                // Allow time for the message to be processed
                Thread.sleep(2000);

                Customer customer = customerService.read(1);
                // Assuming the service doesn't overwrite existing records, the customer's name
                // should not be "John Doe"
                Assert.assertNotEquals("Jane", customer.getFirstName());
                Assert.assertNotEquals("Smith", customer.getLastName());
        }

        @Test
        public void testNegativeCondition() throws InterruptedException {
                // Negative condition: Sending a malformed message
                String msg = "This is not a valid message";

                transactionTemplate.executeWithoutResult(status -> {
                        jmsTemplate.send("customer.create", session -> session.createTextMessage(msg));
                });

                // Allow time for the message to be processed
                Thread.sleep(2000);

                // Assuming the service handles errors gracefully, there should be no customer
                // with ID 999
                Customer customer = customerService.read(999);
                Assert.assertNull(customer);
        }

}