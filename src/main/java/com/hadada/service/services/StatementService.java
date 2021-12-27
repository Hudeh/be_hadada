package com.hadada.service.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hadada.service.modal.WidgetStatementResponse;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class StatementService {
    private final RestTemplate restTemplate;

    @Value("${app.accessToken}")
    String accessToken;

    public WidgetStatementResponse saveBalanceAndTransaction
            (String bankUrl, String bankName, String username, String password, String startDate, String endDate, String callBackUrl) throws IOException {
        WidgetStatementResponse widgetResponse = new WidgetStatementResponse();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject statementJsonObject = new JSONObject();
        statementJsonObject.put("username", username);
        statementJsonObject.put("password", password);
        statementJsonObject.put("start_date", startDate);
        statementJsonObject.put("end_date", endDate);

        String url = bankUrl + bankName + "/";

        HttpEntity<String> request = new HttpEntity<String>(statementJsonObject.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class, 1);

        if(response.getBody() != null){
            JsonNode root = new ObjectMapper().readTree(response.getBody());

            widgetResponse.setSuccess(true);
            widgetResponse.setAccountId(root.path("username").toString());
            widgetResponse.setMessage("Created statement");
            widgetResponse.setStatus(HttpStatus.CREATED.getReasonPhrase());
            widgetResponse.setCallBackUrl(callBackUrl);
            widgetResponse.setBankName(bankName);

        }else {
            widgetResponse.setSuccess(false);
            widgetResponse.setMessage("Can not create statement.");
            widgetResponse.setStatus(HttpStatus.BAD_REQUEST.getReasonPhrase());
            widgetResponse.setCallBackUrl(callBackUrl);

        }
        return widgetResponse;
    }
}
