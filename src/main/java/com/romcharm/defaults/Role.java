package com.romcharm.defaults;

public enum Role {
    ROLE_ROMCHARM_APP("ROLE_ROMCHARM_APP"),
    ROLE_MYPAGE_APP("ROLE_MYPAGE_APP"),
    ROLE_ADMIN("ROLE_ADMIN");

    private String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
