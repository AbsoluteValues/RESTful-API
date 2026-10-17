package com.AbsoluteValue.RESTful.common.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @NotBlank
        @Size(min = 1, max = 10)
        String id,

        @NotBlank
        @Size(min = 1, max = 72)
        String password
) {
}
