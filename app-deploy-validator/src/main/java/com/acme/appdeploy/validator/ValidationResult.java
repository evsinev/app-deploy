package com.acme.appdeploy.validator;

import java.util.ArrayList;
import java.util.List;

/** Accumulates all findings from a validation run. Does not stop on the first error. */
public class ValidationResult {

    private final List<ValidationMessage> messages = new ArrayList<>();

    public void add(ValidationMessage message) {
        messages.add(message);
    }

    public void error(String file, String path, String message) {
        add(ValidationMessage.error(file, path, message));
    }

    public void warning(String file, String path, String message) {
        add(ValidationMessage.warning(file, path, message));
    }

    public List<ValidationMessage> messages() {
        return messages;
    }

    public long errorCount() {
        return messages.stream().filter(it -> it.severity() == Severity.ERROR).count();
    }

    public long warningCount() {
        return messages.stream().filter(it -> it.severity() == Severity.WARNING).count();
    }

    public boolean hasErrors() {
        return errorCount() > 0;
    }

    /** Renders a human readable report. */
    public String format() {
        StringBuilder sb = new StringBuilder();
        for (ValidationMessage message : messages) {
            sb.append(message).append('\n');
        }
        sb.append(String.format("%d error(s), %d warning(s)", errorCount(), warningCount()));
        return sb.toString();
    }
}
