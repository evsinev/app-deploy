package com.acme.appdeploy.ui.service.deploy.messages;

import com.acme.appdeploy.ui.service.deploy.model.DeployListItem;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class DeployListResponse {
    List<DeployListItem> deploys;
}
