package com.hadada.service.modal;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customerid ", columnDefinition = "serial")
    private Long customerId ;

    @Column(name = "username")
    private String username ;

    @Column(name = "password ")
    private String password ;

    @Column(name = "authkey")
    private String authKey ;

    @Column(name = "rolekey")
    private Long roleKey ;

    @Column(name = "pin")
    private String pin;

    @Column(name = "clientid")
    private String clientId;

    @Transient
    private String status;

    @Transient
    private String token;

    @Column(name = "wallet")
    private Long wallet;

    @Column(name = "callbackurl")
    private String callBackUrl;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "brand_name")
    private String brandName;
}
