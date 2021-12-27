package com.hadada.service.modal;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class DownloadStatementRequest {
    @NotBlank(message = "username may not be blank")
    String username;
    @NotBlank(message = "bankName may not be blank")
    String bankName;
    @NotBlank(message = "appKey may not be blank")
    String appKey;
    @NotBlank(message = "authKey may not be blank")
    String authKey;
    String officialEmail;
    String email;
}
