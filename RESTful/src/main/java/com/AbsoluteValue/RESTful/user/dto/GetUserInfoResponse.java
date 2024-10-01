package com.AbsoluteValue.RESTful.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class GetUserInfoResponse {

    private String id;
    private String nickname;
}
