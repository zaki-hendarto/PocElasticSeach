package org.hendarto.poc.elasticsearch.document;

import java.util.List;

public class SuggestWord {
    public List<String> getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(List<String> suggestion) {
        this.suggestion = suggestion;
    }

    List<String> suggestion;
}
