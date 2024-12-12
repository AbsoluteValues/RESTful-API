package com.AbsoluteValue.RESTful.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_tbl")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "id", length = 10)
    private String id;

    @Column(name = "password", length = 500, nullable = false)
    private String password;

    @Column(name = "nickname", length = 10)
    private String nickname;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "email", length = 320)
    private String email;

    @Column(name = "avatar", columnDefinition = "mediumtext")
    private String avatar;
}
