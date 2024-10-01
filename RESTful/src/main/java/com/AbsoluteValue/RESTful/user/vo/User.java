package com.AbsoluteValue.RESTful.user.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class User {

    private String id;
    private String password;
    private String nickname;
    private String address;
    private String phone;
    private String email;
    private String avatar;
}
