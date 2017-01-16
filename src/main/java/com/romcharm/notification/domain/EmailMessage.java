package com.romcharm.notification.domain;

import lombok.Data;

@Data
public class EmailMessage {
    private final String email;
    private final String firstName;
    private final String lastName;
    private final boolean areAttenting;
    private final int numberAttending;
}
