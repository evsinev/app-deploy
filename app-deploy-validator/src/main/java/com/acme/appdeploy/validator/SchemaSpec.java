package com.acme.appdeploy.validator;

import com.google.gson.JsonElement;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Declarative description of the known config structure, used purely to flag <em>unknown</em> keys
 * (typos). It does not check types or required fields - that is done against the parsed entities in
 * {@link ConfigValidator}. Keep the field names here in sync with the entity classes in
 * {@code com.acme.appdeploy.core.dao.config}.
 */
final class SchemaSpec {

    /** A scalar leaf - no keys to check below it. */
    static final SchemaSpec SCALAR = new SchemaSpec(null, null);

    /** Non-null for objects: allowed key -> child spec. */
    private final Map<String, SchemaSpec> fields;

    /** Non-null for arrays: spec applied to every element. */
    private final SchemaSpec item;

    private SchemaSpec(Map<String, SchemaSpec> fields, SchemaSpec item) {
        this.fields = fields;
        this.item   = item;
    }

    static SchemaSpec obj(Object... keyValuePairs) {
        Map<String, SchemaSpec> fields = new LinkedHashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            fields.put((String) keyValuePairs[i], (SchemaSpec) keyValuePairs[i + 1]);
        }
        return new SchemaSpec(fields, null);
    }

    static SchemaSpec arr(SchemaSpec item) {
        return new SchemaSpec(null, item);
    }

    /** Walks the raw tree and reports any key not present in this schema. */
    void checkUnknownKeys(JsonElement element, String path, String file, ValidationResult result) {
        if (fields != null && element.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                String key       = entry.getKey();
                String childPath = path.isEmpty() ? key : path + "." + key;
                SchemaSpec child = fields.get(key);
                if (child == null) {
                    result.error(file, childPath, "unknown key '" + key + "'");
                } else {
                    child.checkUnknownKeys(entry.getValue(), childPath, file, result);
                }
            }
        } else if (item != null && element.isJsonArray()) {
            int index = 0;
            for (JsonElement child : element.getAsJsonArray()) {
                item.checkUnknownKeys(child, path + "[" + index + "]", file, result);
                index++;
            }
        }
        // type mismatches (object where array expected, etc.) are left to typed checks
    }

    // ---- schema definitions ------------------------------------------------

    static final SchemaSpec AUTHS = obj(
            "auths", arr(obj(
                    "authId",      SCALAR,
                    "authType",    SCALAR,
                    "basic",       obj("username", SCALAR, "password", SCALAR),
                    "bearerToken", SCALAR,
                    "apiKey",      SCALAR
            ))
    );

    static final SchemaSpec APP = obj(
            "appId", SCALAR,
            "versionFetching", obj(
                    "type", SCALAR,
                    "nginx", obj(
                            "dirUrl",  SCALAR,
                            "prefix",  SCALAR,
                            "suffix",  SCALAR,
                            "authRef", SCALAR
                    ),
                    "mavenMetadataXml", obj(
                            "url",          SCALAR,
                            "prefix",       SCALAR,
                            "removePrefix", SCALAR,
                            "removeSuffix", SCALAR,
                            "authRef",      SCALAR
                    )
            ),
            "artifact", obj(
                    "artifactUrl", SCALAR,
                    "authRef",     SCALAR
            ),
            "envs", arr(obj(
                    "envName",        SCALAR,
                    "healthCheckUrl", SCALAR,
                    "instances", arr(obj(
                            "instanceName", SCALAR,
                            "deploy", obj(
                                    "deployType", SCALAR,
                                    "dcAgentUrl", obj("url", SCALAR, "authRef", SCALAR)
                            ),
                            "appStatus", obj(
                                    "type",    SCALAR,
                                    "url",     SCALAR,
                                    "authRef", SCALAR
                            )
                    ))
            ))
    );
}
