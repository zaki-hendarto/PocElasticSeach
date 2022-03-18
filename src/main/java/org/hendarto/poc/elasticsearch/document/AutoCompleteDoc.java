package org.hendarto.poc.elasticsearch.document;

import java.io.Serializable;
import java.util.List;

public class AutoCompleteDoc implements Serializable {
    private List<String> keywords;

    public void setKeywords(List<String> keywords){
        this.keywords = keywords;
    }
    public List<String> getKeywords(){
       return keywords;
    }
}
