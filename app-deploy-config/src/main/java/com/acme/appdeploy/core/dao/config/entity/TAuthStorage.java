package com.acme.appdeploy.core.dao.config.entity;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class TAuthStorage {
    List<TAuth> auths;
}
