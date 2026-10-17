package com.AbsoluteValue.RESTful.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpUserRequest(

        @NotBlank
        @Size(min = 1, max = 10)
        String id,

        @NotBlank
        @Size(min = 1, max = 72)
        @Pattern(regexp = "^[!-~]+$", message = "비밀번호에는 공백 없이 영문, 숫자, 특수문자만 사용할 수 있습니다.")
        String password,

        @NotBlank
        @Size(min = 1, max = 10)
        String nickname
) {
}
