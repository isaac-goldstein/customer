package com.acme.customer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

    private CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/customer")
    public ResponseEntity<Void> createCustomer(@RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName, @RequestParam("id") Integer id) {

        Customer c = new Customer();
        c.setFirstName(firstName);
        c.setLastName(lastName);
        c.setId(id);
        customerService.create(c);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Retrieves a customer with the specified ID.
     *
     * @param id the ID of the customer to retrieve
     * @return a ResponseEntity containing the customer and HTTP status code
     */
    @GetMapping(value = "/customer/{id}", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> getCustomer(@PathVariable Integer id) {
        return new ResponseEntity<>(customerService.read(id), HttpStatus.OK);
    }

    /**
     * Deletes a customer with the specified ID.
     *
     * @param id the ID of the customer to delete
     * @return a ResponseEntity containing HTTP status code
     */
    @DeleteMapping("/customer/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer id) {
        customerService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



}
