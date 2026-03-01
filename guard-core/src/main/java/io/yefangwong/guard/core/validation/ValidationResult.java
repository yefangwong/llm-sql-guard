package io.yefangwong.guard.core.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * SQL 校驗結果。
 */
public class ValidationResult {
    private boolean valid = true;
    private List<String> errors = new ArrayList<>();
    private List<String> suggestions = new ArrayList<>();

    public ValidationResult() {}

    public void addError(String error) {
        this.valid = false;
        this.errors.add(error);
    }

    public void addSuggestion(String suggestion) {
        this.suggestions.add(suggestion);
    }

    public boolean isValid() { return valid; }
    public boolean isSuccess() { return valid; }
    public List<String> getErrors() { return errors; }
    public List<String> getSuggestions() { return suggestions; }
}
