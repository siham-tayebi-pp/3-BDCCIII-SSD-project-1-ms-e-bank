package net.tayebi.ebankservice.controllers;

import net.tayebi.ebankservice.entities.BankAccount;
import net.tayebi.ebankservice.repositories.BankAccountRepository;
import net.tayebi.ebankservice.services.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BankAccountController {
    @Autowired
    private BankAccountService bankAccountService;

    @GetMapping("/accounts")
    public List<BankAccount> getAllBankAccounts(){
        return bankAccountService.getAllBankAccounts();
    }

    @GetMapping("/accounts/{id}")
    public BankAccount getBankAccountById(@PathVariable String id){
        return bankAccountService.getBankAccountById(id);
    }

    @PostMapping("/accounts")
    public BankAccount save(@RequestBody  BankAccount bankAccount){
        return bankAccountService.save(bankAccount);
    }

}
