package net.tayebi.ebankservice.feign;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import net.tayebi.ebankservice.model.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service")

public interface CustomerRestClient {
    @GetMapping("/customers/{id}")
    @CircuitBreaker(name = "customer-service",fallbackMethod = "defaultCustomerMethod")
    public Customer getCustomer(@PathVariable long id) ;
    default  Customer defaultCustomerMethod(long id,Exception e ) {
        return new Customer(id,"Default name","default@gmail.com");

    }
}
