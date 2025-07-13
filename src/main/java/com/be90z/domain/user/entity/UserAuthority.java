package com.be90z.domain.user.entity;

public enum UserAuthority {
    USER("user"),
    ADMIN("admin");

    private final String value;

    UserAuthority(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}