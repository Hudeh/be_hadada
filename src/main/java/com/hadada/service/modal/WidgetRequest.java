package com.hadada.service.modal;

import lombok.Getter;
import lombok.Setter;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
@Getter
@Setter
public class WidgetRequest {
    @NotBlank(message = "username may not be blank")
    String username;
    @NotBlank(message = "password may not be blank")
    String password;
    @NotBlank(message = "bankName may not be blank")
    String bankName;
    @NotBlank(message = "appKey may not be blank")
    String appKey;
    @NotBlank(message = "authKey may not be blank")
    String authKey;
    String officialEmail;
    String startDate;
    String endDate;
}
