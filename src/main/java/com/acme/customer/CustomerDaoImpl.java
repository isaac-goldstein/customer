package com.acme.customer;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class CustomerDaoImpl implements CustomerDao {

    private JdbcTemplate jdbcTemplate;

    private static final String CREATE_SQL = "INSERT INTO customer (id, first_name, last_name) VALUES (?, ?, ?)";
    private static final String READ_SQL = "SELECT * FROM customer WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM customer WHERE id = ?";

    public CustomerDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int create(Customer customer) {
        return jdbcTemplate.update(CREATE_SQL, customer.getId(), customer.getFirstName(), customer.getLastName());
    }

    @Override
    public Customer read(int customerId) {
        try {
            return jdbcTemplate.queryForObject(READ_SQL, (rs, rowNum) -> {
                Customer customer = new Customer();
                customer.setId(rs.getInt("id"));
                customer.setFirstName(rs.getString("first_name"));
                customer.setLastName(rs.getString("last_name"));
                return customer;
            }, customerId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void delete(int customerId) {
        jdbcTemplate.update(DELETE_SQL, customerId);
    }
}
