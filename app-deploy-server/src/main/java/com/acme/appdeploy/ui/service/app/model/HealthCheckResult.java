package com.acme.appdeploy.ui.service.app.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class HealthCheckResult {
    HealthCheckResultType type;
    String                errorMessage;
}
