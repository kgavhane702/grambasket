package com.grambasket.userservice.dto;

import lombok.Data;

@Data
public class CommunicationPreferencesDto {
    private boolean emailEnabled;
    private boolean smsEnabled;
    private boolean pushNotificationsEnabled;
}