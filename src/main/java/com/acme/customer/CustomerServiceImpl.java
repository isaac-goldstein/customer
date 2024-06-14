package com.acme.customer;

public class CustomerServiceImpl implements CustomerService {

    private CustomerDao customerDao;

    public CustomerServiceImpl(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public int delete(int customerId) {
        return customerDao.delete(customerId);
    }

    @Override
    public Customer read(Integer customerId) {
        return customerDao.read(customerId);
    }

    @Override
    public int create(Customer customer) {
        // Check if the customer already exists
        int result = 0;
        Customer existingCustomer = customerDao.read(customer.getId());
        if (existingCustomer == null) {
            result = customerDao.create(customer);
        }
        return result;
    }

}
