package com.ryverbank.accounts;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.util.MultiValueMap;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

// Auth Imports
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// JSON Imports
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

@RestController
public class TransfersController {
    @Autowired
    private TransfersRepository transfersRepository;

    @Autowired
    private AccountsRepository accRepository;

    private JSONObject userObj;
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
    
    public boolean verifyOwnership(int account_id, String AuthHeader) {
        
        // verifies that current signed-in user owns account
        int userID = getUID(AuthHeader);
        // int userID = 903;
        Optional<Account> accountEntity = accRepository.findById(account_id);
        if (accountEntity.isPresent()) {
            int customerID = accountEntity.get().getCustomer_id();
            if (userID == customerID) {
                return true;
            }
        }

        return false;
    }
    
    public TransfersController() {}

    @GetMapping(path="/accounts/{id}/transactions")
    public @ResponseBody Iterable<Transfer> getTransfers(@RequestHeader("Authentication") String AuthHeader, @PathVariable int id) {

        if (!verifyOwnership(id, AuthHeader)) {
            System.out.println("403: Forbidden");
            return null;
        }

        Iterable<Transfer> transfers = transfersRepository.findAll();
        Iterator<Transfer> iter = transfers.iterator();

        while (iter.hasNext()) {
            Transfer transfer = iter.next();
            if (transfer.getFrom() != id && transfer.getTo() != id) {
                iter.remove();
            }
        }

        return transfers;
    }

    @PostMapping(path="/accounts/{id}/transactions")
    public @ResponseBody String createTransfer(@RequestBody Transfer transfer, @RequestHeader("Authentication") String AuthHeader) {
        
        // allow user to only transfer from their own accounts
        if (!verifyOwnership(transfer.getFrom(), AuthHeader)) {
            return "Unable to facilitate transfer from account which user does not own.";
        }

        Optional<Account> sender_account = accRepository.findById(transfer.getFrom());
        Optional<Account> receiver_account = accRepository.findById(transfer.getTo());
        if (!sender_account.isPresent()) throw new AccountNotFoundException(transfer.getFrom());
        if (!receiver_account.isPresent()) throw new AccountNotFoundException(transfer.getTo());

        double transfer_amount = transfer.getAmount();

        Account sender = sender_account.get();
        Account receiver = receiver_account.get();

        if (sender.getAvailable_balance() < transfer_amount) {
            return "Insufficient funds for transfer";
        }
        
        sender.updateBalance(-transfer_amount);
        receiver.updateBalance(transfer_amount);

        transfersRepository.save(transfer);

        return "Funds transferred successfully.";
    }
}