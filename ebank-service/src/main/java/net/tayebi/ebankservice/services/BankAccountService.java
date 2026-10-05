package net.tayebi.ebankservice.services;

import net.tayebi.ebankservice.entities.BankAccount;
import net.tayebi.ebankservice.repositories.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class BankAccountService {
    @Autowired
    private BankAccountRepository accountRepository;


    public List<BankAccount> getAllBankAccounts(){
        return accountRepository.findAll();
    }

    public BankAccount getBankAccountById(String id){
            return accountRepository.findById(id)
                    .orElseThrow(()->new RuntimeException("Account not found"));
    }

    public BankAccount save(BankAccount bankAccount){
                bankAccount.setId(UUID.randomUUID().toString());
                bankAccount.setCreatedAt(new Date());
                return accountRepository.save(bankAccount);
    }
}

