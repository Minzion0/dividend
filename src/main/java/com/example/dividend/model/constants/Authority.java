package com.example.dividend.model.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum Authority {
    ROLE_READ("READ"),
    ROLE_WRITE("WRITE");

    private final String role;

}
