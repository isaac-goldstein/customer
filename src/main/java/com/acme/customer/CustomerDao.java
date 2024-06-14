package com.acme.customer;



public interface CustomerDao {
    int create(Customer customer);

    Customer read(int id);

    int delete(int id);

}
