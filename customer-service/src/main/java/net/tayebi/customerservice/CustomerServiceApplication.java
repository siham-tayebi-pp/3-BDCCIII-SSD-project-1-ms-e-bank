package net.tayebi.customerservice;

import net.tayebi.customerservice.entities.Customer;
import net.tayebi.customerservice.repositories.CustomerRepository;
import net.tayebi.customerservice.services.CustomerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
    @Bean
    public CommandLineRunner start(CustomerService customerService) {
        return args -> {
           List<String> names= List.of("siham","imane","omar","ali","fouzia" );
           names.forEach(name -> {
               customerService.createCustomer(
                       Customer.builder()
                               .name(name)
                               .email(name+"@gmail.com")
                               .build()
               );
           });
        };

    }

}
