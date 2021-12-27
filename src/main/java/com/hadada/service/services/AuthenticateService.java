package com.hadada.service.services;


import java.util.List;

import com.hadada.service.encrypt.EncryptDecrypt;
import com.hadada.service.modal.Customer;
import com.hadada.service.modal.DownloadStatementRequest;
import com.hadada.service.modal.WidgetRequest;
import com.hadada.service.modal.HadadaServiceResponse;
import com.hadada.service.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ListIterator;

@Service
public class AuthenticateService{

    @Autowired
    private CustomerRepository customerRepository;

    @Value("${api.cost}")
    Long apiCost;

    public Boolean authenticateWithAuthPortal(DownloadStatementRequest downloadStatementRequest, HadadaServiceResponse hadadaServiceResponse) {
        List<Customer> customerList = customerRepository.findAll();

        for (Customer customer : customerList) {
            if (downloadStatementRequest.getAuthKey().equals(EncryptDecrypt.decryptKey(customer.getAuthKey()))) {
                hadadaServiceResponse.setWalletStatus("valid wallet balance");
                hadadaServiceResponse.setEmail(customer.getUsername());
                return true;
            }
        }
        return false;
    }

    public Boolean authenticateWithAuthWidget(WidgetRequest widgetRequest, HadadaServiceResponse hadadaServiceResponse) {
        List<Customer> customerList = customerRepository.findAll();

        for (Customer customer : customerList) {
            if (widgetRequest.getAuthKey().equals(EncryptDecrypt.decryptKey(customer.getAuthKey()))) {
                hadadaServiceResponse.setWalletStatus("valid wallet balance");
                hadadaServiceResponse.setEmail(customer.getUsername());
                hadadaServiceResponse.setCallBackUrl(customer.getCallBackUrl());
                return true;
            }
        }
        return false;
    }
}
