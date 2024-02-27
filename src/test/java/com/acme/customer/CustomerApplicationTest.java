package com.acme.customer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:customer-test-context.xml")
// @TestExecutionListeners(listeners = {
// DependencyInjectionTestExecutionListener.class,
// TransactionalTestExecutionListener.class })
public class CustomerApplicationTest {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @Transactional
    public void testSimpleBusiness() {
        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setId(11);

        int i = customerService.create(customer);
        Assert.assertEquals(i, 1);
    }

    @Test
    @Transactional
    public void testSimpleControllerAdd() throws Exception {
        mockMvc.perform(post("/customer")
                .param("id", "11")
                .param("firstName", "John")
                .param("lastName", "Doe").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    public void testSimpleControllerAddAndRetrieve() throws Exception {

        mockMvc.perform(post("/customer")
                .param("id", "11")
                .param("firstName", "John")
                .param("lastName", "Doe").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/customer/{id}", 11).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("11"))
                .andReturn();

    }

    @Test
    public void testSimpleJms() throws InterruptedException {

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        String msg = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"id\":11}";

        transactionTemplate.executeWithoutResult(status -> {
            jmsTemplate.send("customer.create", session -> session.createTextMessage(msg));
        });

        // Allow time for the message to be processed
        Thread.sleep(2000);
        Customer customer = customerService.read(11);
        Assert.assertNotNull(customer);

        // cleanup - must be transactional
        transactionTemplate.executeWithoutResult(status -> {
            customerService.delete(11);
        });

        customer = customerService.read(11);
        Assert.assertNull(customer);

    }

}
