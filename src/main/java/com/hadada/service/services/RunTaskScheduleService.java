package com.hadada.service.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hadada.service.modal.WidgetRequest;
import com.hadada.service.modal.WidgetStatementResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RunTaskScheduleService {
    private final RestTemplate restTemplate;
    private static final String SCHEDULER_CRON_MINUTELY = "0 * * * * *";
    private final HadadaService hadadaService;

    private static final Logger logger = LoggerFactory.getLogger(RunTaskScheduleService.class);

    @Value("${app.accessToken}")
    String accessToken;
    @Value("${app.bankUrl}")
    String bankUrl;

    @Scheduled(cron = SCHEDULER_CRON_MINUTELY)
    public void runTaskSchedule() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("accept", String.valueOf(MediaType.APPLICATION_JSON));
            HttpEntity request = new HttpEntity(headers);

            String url = bankUrl + "/verify/get_verify_login";

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    String.class,
                    1
            );

            JsonNode root = new ObjectMapper().readTree(response.getBody());
            Iterator<JsonNode> verifiedUsers = root.elements();

            while (verifiedUsers.hasNext()) {
                JsonNode verifiedUser = verifiedUsers.next();
                System.out.println(verifiedUser.toString());
                LocalDate todayDate = LocalDate.now();
                LocalDate sixMonthAgo= todayDate.minusMonths(6);

                WidgetRequest widgetRequest = new WidgetRequest();
                widgetRequest.setAuthKey(verifiedUser.path("authKey").toString());
                widgetRequest.setBankName(verifiedUser.path("bank").toString());
                widgetRequest.setUsername(verifiedUser.path("username").toString());
                widgetRequest.setPassword(verifiedUser.path("password").toString());
                widgetRequest.setEndDate(todayDate.toString());
                widgetRequest.setStartDate(sixMonthAgo.toString());

                WidgetStatementResponse res = hadadaService.saveStatementApi(widgetRequest, "transaction");

                logger.info(res.toString());
            }
        } catch (Exception e) {

        }
    }
}
