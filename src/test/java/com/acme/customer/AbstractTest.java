package com.acme.customer;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:customer-context.xml", "classpath:test-message-context.xml",
        "classpath:test-datasource-context.xml", "classpath:customer-test-context.xml" })
// @Transactional
public abstract class AbstractTest {

}
