package com.hadada.service.modal;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "statement")
public class Statement {
    @Id
    @GeneratedValue
    @Column(name = "statementid")
    private Long statementId;

    @Column(name = "email")
    private String email;

    @Column(name = "statementname")
    private String statementName;

    @Column(name = "description")
    private String description;

    @Column(name = "receivingemail")
    private String receivingEmail;

    @Column(name = "redirecturl")
    private String redirectUrl;

    @Column(name = "duration")
    private String duration;

    @Column(name = "sessionkey")
    private String sessionKey;

    @Transient
    private String authKey;
}
