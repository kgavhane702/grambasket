package com.grambasket.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunicationPreferences {

    @Builder.Default
    private boolean emailForOrderUpdates = true;

    @Builder.Default
    private boolean emailForPromotions = false;

    @Builder.Default
    private boolean smsForOrderUpdates = true;

    @Builder.Default
    private boolean pushForOrderUpdates = true;

    @Builder.Default
    private boolean pushForPromotions = false;
}