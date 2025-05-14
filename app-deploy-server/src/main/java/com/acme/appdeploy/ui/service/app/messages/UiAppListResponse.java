package com.acme.appdeploy.ui.service.app.messages;

import com.acme.appdeploy.ui.service.app.model.UiAppItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
@AllArgsConstructor
public class UiAppListResponse {
    List<UiAppItem> apps;
}
