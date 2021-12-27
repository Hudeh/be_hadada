package com.hadada.service.repositories;

import com.hadada.service.modal.Statement;
import org.springframework.data.repository.CrudRepository;

public interface StatementRepository extends CrudRepository<Statement, Long> {
    Statement findBySessionKey(String sessionKey);
}
