package com.hadada.service.services;

import com.hadada.service.dto.LenderDetails;
import com.hadada.service.encrypt.EncryptDecrypt;
import com.hadada.service.modal.Customer;
import com.hadada.service.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LenderService {
    private final CustomerRepository repository;
    private final ModelMapper modelMapper;

    public LenderDetails getLenderDetails(String authToken){
        List<Customer> customerList = repository.findAll();

        for (Customer customer : customerList) {
            if (authToken.equals(EncryptDecrypt.decryptKey(customer.getAuthKey()))) {
                return modelMapper.map(customer, LenderDetails.class);
            }
        }
        return null;
    }
}
