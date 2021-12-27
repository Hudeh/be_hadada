package com.hadada.service.services;

import com.hadada.service.dto.WidgetUsersDto;
import com.hadada.service.modal.WidgetUsers;
import com.hadada.service.repositories.WidgetUsersRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WidgetUsersService {
    private final WidgetUsersRepository repository;
    private final ModelMapper modelMapper;

    public WidgetUsersDto saveWidgetUserEmail(WidgetUsersDto usersDto){
        return modelMapper.map(repository.save(modelMapper.map(usersDto, WidgetUsers.class)), WidgetUsersDto.class);
    }
}
