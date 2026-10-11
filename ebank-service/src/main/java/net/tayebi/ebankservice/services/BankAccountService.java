package net.tayebi.ebankservice.services;

import lombok.AllArgsConstructor;
import net.tayebi.ebankservice.entities.BankAccount;
import net.tayebi.ebankservice.feign.CustomerRestClient;
import net.tayebi.ebankservice.model.Customer;
import net.tayebi.ebankservice.repositories.BankAccountRepository;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor

public class BankAccountService {

    private BankAccountRepository accountRepository;
    private CustomerRestClient customerRestClient;

    @McpTool(description = "Get All Bank accounts")
    public List<BankAccount> getAllBankAccounts(){
        return accountRepository.findAll();
    }

    @McpTool(description = "Find Account By Id")
    public BankAccount getBankAccountById(@McpToolParam(description = "The account id") String id){
            BankAccount bankAccount= accountRepository.findById(id)
                    .orElseThrow(()->new RuntimeException("Account not found"));
            bankAccount.setCustomer(customerRestClient.getCustomer(bankAccount.getCustomerId()));
        return bankAccount;
    }

    @McpTool(description = "Save new account")
    public BankAccount save(@McpToolParam(description = "The bank account to save(balance, type, customerId)") BankAccount bankAccount){
        try {
            Customer customer = customerRestClient.getCustomer(bankAccount.getCustomerId());
            bankAccount.setId(UUID.randomUUID().toString());
            bankAccount.setCreatedAt(new Date());
            return accountRepository.save(bankAccount);
            
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

