package com.hadada.service.repositories;

import com.hadada.service.modal.CollectedPDF;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CollectedPdfRepository extends CrudRepository<CollectedPDF, Long> {
    List<CollectedPDF> findBySessionKey(String sessionKey);
}
