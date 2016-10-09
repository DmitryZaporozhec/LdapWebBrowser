package kursovoy.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by zaporozhec on 10/8/16.
 */
public class LdapNode implements Serializable {
    String id;
    Map<String, List<String>> attributes;


    public Map<String, List<String>> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, List<String>> attributes) {
        this.attributes = attributes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
