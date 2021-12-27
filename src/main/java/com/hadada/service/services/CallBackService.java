package com.hadada.service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class CallBackService{
    @Autowired
    private RestTemplate restTemplate;

    public Boolean sendDataToCallbackUrl(String data, String callbackUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity request = new HttpEntity(data,headers);

        try{
            restTemplate.exchange(
                callbackUrl,
                HttpMethod.POST,
                request,
                String.class,
                1
           );
        } catch (Exception e){
            return false;
        }
        return true;
    }
}
