package com.hadada.service.modal;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@Table(name = "widget_users")
public class WidgetUsers {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "email_address")
    private Long email;

    @Column(name = "createdate")
    private Date createdDate;
}
