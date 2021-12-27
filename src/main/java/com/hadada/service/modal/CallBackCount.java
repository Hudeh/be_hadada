package com.hadada.service.modal;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "callbackcount")

public class CallBackCount {
    @Id
    @GeneratedValue
    @Column(name = "callbackcountid")
    private Long callBackCountId;

    @Column(name = "appid")
    private Long appId;

    @Column(name = "callcount")
    private Integer callCount;

    @Column(name = "callbackdate")
    private String callBackDate;

    @Column(name = "sessionkey")
    private String sessionKey;

}
