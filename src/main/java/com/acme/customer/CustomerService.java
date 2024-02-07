
package com.acme.customer;

import org.springframework.transaction.annotation.Transactional;

public interface CustomerService {

    @Transactional
    int create(Customer customer);

    @Transactional(readOnly = true)
    Customer read(Integer customerId);

    @Transactional    
    void delete(int customerId);
}
