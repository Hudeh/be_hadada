package com.hadada.service.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hadada.service.modal.App;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

import org.springframework.web.client.RestTemplate;

import java.util.Iterator;

@Service
public class BalanceAndTransactionService {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${app.accessToken}")
    String accessToken;

    public String getTransaction(String bankUrl, String bankName, String username, String appName) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("accept", String.valueOf(MediaType.APPLICATION_JSON));
        HttpEntity request = new HttpEntity(headers);

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode transactionArray = objectMapper.createArrayNode();
        String url = bankUrl + bankName + "/user/get_transaction/" + username;

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class,
                1
        );

        JsonNode root = new ObjectMapper().readTree(response.getBody());
        JsonNode transaction_data = root.path("transaction_data");
        JsonNode data = transaction_data.path("data");
        Iterator<JsonNode> fields = data.elements();

        while (fields.hasNext()) {
            JsonNode field = fields.next();
            JsonNode transaction = field.path("transaction");
            Iterator<JsonNode> transactions = transaction.elements();
            while (transactions.hasNext()) {
                JsonNode trans = transactions.next();
                ObjectNode object = (ObjectNode) trans;
                transactionArray.add(object);
            }
        }

        return transactionArray.toString();
    }

    public String getBalance(String bankUrl, String bankName, String username, String appName) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("accept", String.valueOf(MediaType.APPLICATION_JSON));
        HttpEntity request = new HttpEntity(headers);

        String url = bankUrl + bankName + "/user/get_balance/" + username;

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class,
                1
        );

        JsonNode root = new ObjectMapper().readTree(response.getBody());
        JsonNode transaction_data = root.path("balance_data");
        JsonNode user = transaction_data.path("user");
        JsonNode accounts = user.path("accounts");
        Iterator<JsonNode> fields = accounts.elements();
        ObjectNode object = null;

        while (fields.hasNext()) {
            JsonNode field = fields.next();
            JsonNode balance = field.path("balance");
            object = (ObjectNode) balance;
        }

        return object.toString();
    }
}
