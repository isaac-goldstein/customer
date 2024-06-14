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
        int cnt = customerService.create(c);
        if (cnt != 1) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
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

        Customer c = customerService.read(id);

        if(c == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(c, HttpStatus.OK);
        }
        
    }   

        // Customer cust = customerService.read(id);

        // HttpHeaders headers = new HttpHeaders();
        // headers.setContentType(MediaType.APPLICATION_JSON);
        // System.out.println(cust);
        // ResponseEntity<Customer> re = new ResponseEntity<>(cust, headers, HttpStatus.OK);

        // System.out.println(re);
        // return re;


    /**
     * Deletes a customer with the specified ID.
     *
     * @param id the ID of the customer to delete
     * @return a ResponseEntity containing HTTP status code
     */
    @DeleteMapping("/customer/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer id) {
        int delCnt = customerService.delete(id);

        if(delCnt == 1) {
            return new ResponseEntity<>(HttpStatus.OK);
        }else if(delCnt == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
