package com.acme.customer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:customer-context.xml", "classpath:test-message-context.xml",
        "classpath:test-datasource-context.xml", "classpath:customer-test-context.xml" })
@Transactional
public class CustomerControllerTest extends AbstractTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void testGetCustomer() throws Exception {
        mockMvc.perform(get("/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // TODO generate a create customer controller test


    @Test
    @Ignore
    public void testUpdateCustomer() throws Exception {
        mockMvc.perform(put("/customer/1")
                .param("firstName", "Jane")
                .param("lastName", "Doe"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteCustomer() throws Exception {
        mockMvc.perform(delete("/customer/1"))
                .andExpect(status().isOk());
    }

    // Edge case: Attempt to get a customer that doesn't exist
    @Test
    public void testGetNonExistentCustomer() throws Exception {
        mockMvc.perform(get("/customer/999"))
                .andExpect(status().isNotFound());
    }

    // Negative case: Attempt to create a customer with invalid data
    @Test
    // @Ignore
    public void testCreateInvalidCustomer() throws Exception {
        mockMvc.perform(post("/customer")
                .param("id", "2")
                .param("firstName", "") // Empty first name
                .param("lastName", "Doe"))
                .andExpect(status().isBadRequest());
    }
}