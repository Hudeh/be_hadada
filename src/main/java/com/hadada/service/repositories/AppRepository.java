package com.hadada.service.repositories;

import com.hadada.service.modal.App;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AppRepository extends CrudRepository<App, Long> {
    App findByAppKey(String appKey);
}
