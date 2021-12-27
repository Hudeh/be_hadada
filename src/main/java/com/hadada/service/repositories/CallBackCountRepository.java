package com.hadada.service.repositories;


import com.hadada.service.modal.CallBackCount;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface CallBackCountRepository extends CrudRepository<CallBackCount, Long> {
    List<CallBackCount> findByAppIdIn(Collection<Long> appIds);
}
