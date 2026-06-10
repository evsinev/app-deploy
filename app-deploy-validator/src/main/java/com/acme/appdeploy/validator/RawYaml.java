package com.acme.appdeploy.validator;

import com.google.gson.JsonObject;
import com.payneteasy.yaml2json.Yaml2GsonConverter;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.api.lowlevel.Compose;
import org.snakeyaml.engine.v2.nodes.MappingNode;
import org.snakeyaml.engine.v2.nodes.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Loads a YAML file into a raw {@link JsonObject} tree, mirroring exactly what
 * {@link com.payneteasy.yaml2json.YamlParser} feeds into Gson. Used for unknown-key detection:
 * Gson silently drops keys it cannot map, so a typo like {@code fetchType} instead of {@code type}
 * is invisible to typed parsing but visible here.
 */
final class RawYaml {

    private RawYaml() {
    }

    static JsonObject load(File file) throws Exception {
        Compose compose = new Compose(LoadSettings.builder().build());
        try (Reader reader = new InputStreamReader(new FileInputStream(file), UTF_8)) {
            Node node = compose.composeReader(reader)
                    .orElseThrow(() -> new IllegalStateException("Empty YAML document"));
            if (!(node instanceof MappingNode mappingNode)) {
                throw new IllegalStateException("Top level YAML node is not a mapping");
            }
            return new Yaml2GsonConverter().convertToJson(mappingNode);
        }
    }
}
