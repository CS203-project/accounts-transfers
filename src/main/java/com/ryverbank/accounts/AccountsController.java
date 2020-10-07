package com.ryverbank.accounts;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.util.MultiValueMap;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;   

import java.util.*;
import java.lang.IllegalArgumentException;

@RestController
public class AccountsController {
    @Autowired
    private AccountsRepository accRepository;

    public AccountsController() {}

    @PostMapping(path="/accounts")
    public @ResponseBody String addAccount (@RequestBody Account account) {
        // in account object, there is customer_id
        // find role from customer_id from userDB or from Account.java
        // if the role is manager, allow creation of account
        // if not, return exception
        accRepository.save(account);
        return "Saved new account";
    }

    
}