package com.hadada.service.services;

import com.hadada.service.exception.CustomHttpClientErrorException;
import com.hadada.service.exception.DataNotFoundException;
import com.hadada.service.modal.*;
import com.hadada.service.repositories.AppRepository;
import com.hadada.service.repositories.CallBackCountRepository;
import com.hadada.service.repositories.CustomerRepository;
import com.hadada.service.repositories.StatementRepository;
import com.hadada.service.util.URLHelper;
import org.json.simple.JSONArray;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class HadadaService {

    @Autowired
    AuthenticateService authenticateService;
    @Autowired
    private AppRepository appRepository;
    @Autowired
    private BalanceAndTransactionService balanceAndTransactionService;
    @Autowired
    private CallBackService callBackService;
    @Autowired
    private CallBackCountRepository callBackCountRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private StatementRepository statementRepository;
    @Autowired
    private StatementService statementService;
    @Autowired
    private ModelMapper modelMapper;

    @Value("${app.env}")
    String env;

    @Value("${app.bankUrl}")
    String bankUrl;

    @Value("${api.cost}")
    Long apiCost;

    public HadadaServiceResponse getBalanceAndTransactionApi(WidgetRequest widgetRequest, String appName) {
        String apiData = null;
        boolean validData = true;
        App app = new App();
        HadadaServiceResponse hadadaServiceResponse = new HadadaServiceResponse();
        if (!verifyApp(widgetRequest, hadadaServiceResponse, app)) {
            return hadadaServiceResponse;
        }
        if (app.getEnvironment().equals(env)) {
            try {
                JSONParser parser = new JSONParser();
                int r = (int) (Math.random() * (3 - 1)) + 1;
                FileReader fileReader = new FileReader("/opt/app/" + appName + r + ".json");
                Object object = parser
                        .parse(fileReader);
                JSONArray jsonArray = (JSONArray) object;
                apiData = jsonArray.toJSONString();
                fileReader.close();
            } catch (Exception e) {
                hadadaServiceResponse.setWalletStatus("");
                hadadaServiceResponse.setStatus("Data sending failed to callback url with POST request." + e);
                return hadadaServiceResponse;
            }
        } else {
            if (hadadaServiceResponse.getWalletStatus().equalsIgnoreCase("You do not have sufficient balance to make api call.")) {
                return hadadaServiceResponse;
            }
            try {
                apiData = balanceAndTransactionService.getTransaction(bankUrl, widgetRequest.getBankName(), widgetRequest.getUsername(), appName);
                Customer customer = customerRepository.findByUsername(hadadaServiceResponse.getEmail());
                Long wallet = customer.getWallet() - apiCost;
                customer.setWallet(wallet);
                customerRepository.save(customer);
            } catch (HttpClientErrorException e) {
                if (e.getRawStatusCode() == 403) {
                    hadadaServiceResponse.setStatus(e.getMessage());
                    return hadadaServiceResponse;
                } else {
                    throw new CustomHttpClientErrorException();
                }

            } catch (Exception e) {
                throw new DataNotFoundException();
            }
        }

        if (!validData || !callBackService.sendDataToCallbackUrl(apiData, app.getCallBackUrl())) {
            hadadaServiceResponse.setWalletStatus("");
            hadadaServiceResponse.setStatus("Data sending failed to callback url with POST request. Please check your callback url");
            return hadadaServiceResponse;
        }

        hadadaServiceResponse.setData(apiData);
        hadadaServiceResponse.setCallBackUrl(app.getCallBackUrl());
        CallBackCount callBackCount = new CallBackCount();
        callBackCount.setCallCount(1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
        Date date = new Date();
        callBackCount.setCallBackDate(dateFormat.format(date));
        callBackCount.setAppId(app.getAppId());
        callBackCountRepository.save(callBackCount);
        hadadaServiceResponse.setStatus("Data sent to callback url");
        return hadadaServiceResponse;
    }

    private HadadaServiceResponse validateLenderWidget(WidgetRequest widgetRequest, HadadaServiceResponse hadadaServiceResponse) {
        if (!authenticateService.authenticateWithAuthWidget(widgetRequest, hadadaServiceResponse)) {
            hadadaServiceResponse.setHadadaAuthentication(false);
            hadadaServiceResponse.setStatus("Invalid auth Key");
        } else {
            hadadaServiceResponse.setHadadaAuthentication(true);
            hadadaServiceResponse.setStatus("Valid auth Key");
        }
        return hadadaServiceResponse;
    }

    private HadadaServiceResponse validateLenderPortal(DownloadStatementRequest downloadStatementRequest, HadadaServiceResponse hadadaServiceResponse) {
        if (!authenticateService.authenticateWithAuthPortal(downloadStatementRequest, hadadaServiceResponse)) {
            hadadaServiceResponse.setHadadaAuthentication(false);
            hadadaServiceResponse.setStatus("Invalid auth Key");
        } else {
            hadadaServiceResponse.setHadadaAuthentication(true);
            hadadaServiceResponse.setStatus("Valid auth Key");
        }
        return hadadaServiceResponse;
    }

    private App getAppDetails(WidgetRequest widgetRequest, HadadaServiceResponse hadadaServiceResponse) {
        App app = appRepository.findByAppKey(widgetRequest.getAppKey());
        if (null != app) {
            hadadaServiceResponse.setStatus("Valid app Key");
            hadadaServiceResponse.setValidAppKey(true);
        } else {
            hadadaServiceResponse.setValidAppKey(false);
            hadadaServiceResponse.setStatus("Invalid app Key");
        }
        return app;
    }

    private Boolean verifyApp(WidgetRequest widgetRequest, HadadaServiceResponse hadadaServiceResponse, App newApp) {
        hadadaServiceResponse = validateLenderWidget(widgetRequest, hadadaServiceResponse);
        if (!hadadaServiceResponse.getHadadaAuthentication()) {
            return false;
        }
        App app = getAppDetails(widgetRequest, hadadaServiceResponse);
        if (!hadadaServiceResponse.getValidAppKey()) {
            return false;
        }
        newApp.setAppId(app.getAppId());
        newApp.setCallBackUrl(app.getCallBackUrl());
        newApp.setEnvironment(app.getEnvironment());
        newApp.setAppType(app.getAppType());
        if (!hadadaServiceResponse.getValidAppKey()) {
            return false;
        }
        if (!URLHelper.isUrlValid(app.getCallBackUrl())) {
            hadadaServiceResponse.setStatus("Invalid callback url");
            return false;
        }
        return true;
    }

    public WidgetStatementResponse saveStatementApi(WidgetRequest widgetRequest, String appName) {
        WidgetStatementResponse apiData = null;
        HadadaServiceResponse hadadaServiceResponse = new HadadaServiceResponse();

        validateLenderWidget(widgetRequest, hadadaServiceResponse);
        if (!hadadaServiceResponse.getHadadaAuthentication() || hadadaServiceResponse.getWalletStatus() != null
                && hadadaServiceResponse.getWalletStatus().equalsIgnoreCase("You do not have sufficient balance to make api call.")) {
            return modelMapper.map(hadadaServiceResponse, WidgetStatementResponse.class);
        }

        try {
            apiData = statementService.saveBalanceAndTransaction(
                    bankUrl,
                    widgetRequest.getBankName(),
                    widgetRequest.getUsername(),
                    widgetRequest.getPassword(),
                    widgetRequest.getStartDate(),
                    widgetRequest.getEndDate(),
                    hadadaServiceResponse.getCallBackUrl()
            );

        } catch (HttpClientErrorException e) {
            if (e.getRawStatusCode() == 403) {
                hadadaServiceResponse.setStatus(e.getMessage());
                return modelMapper.map(hadadaServiceResponse, WidgetStatementResponse.class);
            } else {
                throw new CustomHttpClientErrorException();
            }

        } catch (Exception e) {
            throw new DataNotFoundException();
        }


        CallBackCount callBackCount = new CallBackCount();
        callBackCount.setCallCount(1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        callBackCount.setCallBackDate(dateFormat.format(date));
        callBackCount.setSessionKey(widgetRequest.getAppKey());
        callBackCountRepository.save(callBackCount);
        Customer customer = customerRepository.findByUsername(hadadaServiceResponse.getEmail());
        Long wallet = customer.getWallet()-apiCost;
        customer.setWallet(wallet);
        customerRepository.save(customer);
        return apiData;
    }

    public HadadaServiceResponse getStatementApi(DownloadStatementRequest downloadStatementRequest, String appName) {
        String apiData = null;
        HadadaServiceResponse hadadaServiceResponse = new HadadaServiceResponse();
        validateLenderPortal(downloadStatementRequest, hadadaServiceResponse);

        if (!hadadaServiceResponse.getHadadaAuthentication() || hadadaServiceResponse.getWalletStatus() != null
                && hadadaServiceResponse.getWalletStatus().equalsIgnoreCase("You do not have sufficient balance to make api call.")) {
            return hadadaServiceResponse;
        }

        try {
            apiData = balanceAndTransactionService.getTransaction(bankUrl, downloadStatementRequest.getBankName(), downloadStatementRequest.getUsername(), appName);
        } catch (HttpClientErrorException e) {
            if (e.getRawStatusCode() == 403) {
                hadadaServiceResponse.setStatus(e.getMessage());
                return hadadaServiceResponse;
            } else {
                throw new CustomHttpClientErrorException();
            }

        } catch (Exception e) {
            throw new DataNotFoundException();
        }

        hadadaServiceResponse.setData(apiData);
        hadadaServiceResponse.setStatus("Statement created");

        return hadadaServiceResponse;
    }

}
