package com.acme.customer;

public class CustomerServiceImpl implements CustomerService {

    private CustomerDao customerDao;

    public CustomerServiceImpl(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public void delete(int customerId) {
        customerDao.delete(customerId);
    }

    @Override
    public Customer read(Integer customerId) {
        return customerDao.read(customerId);
    }

    @Override
    public int create(Customer customer) {
        return customerDao.create(customer);
    }

}
