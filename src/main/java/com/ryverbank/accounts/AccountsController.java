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


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// JSON imports
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

import org.springframework.beans.factory.annotation.Autowired;   

import java.util.*;
import java.lang.IllegalArgumentException;

@RestController
public class AccountsController {
    @Autowired
    private AccountsRepository accRepository;
    
    private JSONObject userObj;

    public AccountsController() {}

    public void getCredentials(String jwt) {
        String output = "";
        try {
            URL url = new URL("http://13.212.86.115/api/customers/verification");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authentication", jwt);

            if (conn.getResponseCode() != 200) throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            // System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                setUserObj(output);
            }

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUserObj(String userJSON) {
        JSONParser parser = new JSONParser();
        JSONObject userObj = new JSONObject();
        try {
            Object obj  = parser.parse(userJSON);
            JSONArray array = new JSONArray();
            array.add(obj);

            userObj = (JSONObject)array.get(0);

            this.userObj = userObj;

        } catch(ParseException pe) {
		
            System.out.println("position: " + pe.getPosition());
            System.out.println(pe);
        } catch (NullPointerException npe) {

            System.out.println("No user found.");
        }
    }

    public int getUID(String AuthHeader) {
        getCredentials(AuthHeader);
        if (this.userObj != null) {
            long userLong = (long)this.userObj.get("id");
            int userID = Math.toIntExact(userLong);
            return userID;
        }
        return 0;
    }

    @PostMapping(path="/accounts")
    public @ResponseBody String addAccount (@RequestBody Account account, @RequestHeader("Authentication") String AuthHeader) {

        // issue: how will the manager input customer id of the client?

        // Authenticate
        getCredentials(AuthHeader);

        // Verify role
        if (this.userObj != null) {
            String role = (String)(this.userObj.get("authorities"));
            if (role.compareTo("ROLE_MANAGER") == 0) {

                // create new account
                accRepository.save(account);
                return "New account created";
            }
        }
        
        return "Manager privileges required to create a new Ryverbank account.";
    }

    @GetMapping(path="/accounts")
    public @ResponseBody Iterable<Account> getAccounts(@RequestHeader("Authentication") String AuthHeader) {

        Iterable<Account> accounts = accRepository.findAll();
        Iterator<Account> iter = accounts.iterator();

        int userID = getUID(AuthHeader);
        // int userID = 123456;

        while(iter.hasNext()) {
            Account acc = iter.next();
            if(acc.getCustomer_id() != userID) {
                iter.remove();
            }
        }
        return accounts;
    }

    @GetMapping(path="/accounts/{id}")
    public @ResponseBody Account getAccountsById(@RequestHeader("Authentication") String AuthHeader, @PathVariable int id) {

        int userID = getUID(AuthHeader);
        // int userID = 123456;

        Optional<Account> accountEntity = accRepository.findById(id);
        Account account;
        if (!accountEntity.isPresent()) {
            throw new AccountNotFoundException(id);
        } else {
            account = accountEntity.get();
            if (account.getCustomer_id() != userID) {
                System.out.println("No account of this ID associated with this user.");
                throw new AccountNotFoundException(id);
            }
        }
        
        return account;
    }


}