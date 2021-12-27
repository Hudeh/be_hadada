package com.hadada.service.repositories;


import com.hadada.service.modal.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
    List<Customer> findAll();
    Optional<Customer> findByAuthKey(String authKey);
    Customer findByUsername(String email);
}
