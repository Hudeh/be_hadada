package com.hadada.service.repositories;

import com.hadada.service.modal.WidgetUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WidgetUsersRepository extends JpaRepository<WidgetUsers, Long> {
}
