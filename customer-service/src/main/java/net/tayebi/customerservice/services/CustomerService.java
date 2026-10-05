package net.tayebi.customerservice.services;


import net.tayebi.customerservice.entities.Customer;
import net.tayebi.customerservice.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers(){
            return customerRepository.findAll();
    }

    public Customer findCustomerById(Long id){
                return customerRepository.findById(id)
                        .orElseThrow(()->new RuntimeException(String.format("Customer with id %s not found", id)));
    }
    public  Customer createCustomer(Customer customer){
        return customerRepository.save(customer);
    }
}
