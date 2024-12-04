package com.AbsoluteValue.RESTful.user.vo;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    private String id;
    private String password;
    private String nickname;
    private String address;
    private String phone;
    private String email;
    private String avatar;
}
