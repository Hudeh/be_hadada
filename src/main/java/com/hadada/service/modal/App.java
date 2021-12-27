package com.hadada.service.modal;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "app")
public class App {

    @Id
    @GeneratedValue
    @Column(name = "appid")
    private Long appId;

    @Column(name = "appname")
    private String appName;

    @Column(name = "apptype")
    private String appType;

    @Column(name = "email")
    private String email;

    @Column(name = "callbackurl")
    private String callBackUrl;

    @Column(name = "appkey")
    private String appKey;

    @Column(name = "environment")
    private String environment;

    @Column(name = "appmigratedid")
    private Long appMigratedId;

    @Column(name = "callcount")
    private Integer callCount;

}