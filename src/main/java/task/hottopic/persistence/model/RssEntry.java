package task.hottopic.persistence.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class RssEntry {

    @Id
    @GeneratedValue
    @JsonProperty
    private Long id;
    @JsonProperty
    private String title;
    @Lob
    @JsonProperty
    private String uri;

    public RssEntry(String title, String uri) {
        this.title = title;
        this.uri = uri;
    }

    public RssEntry(){ }

    public String getTitle() {
        return title;
    }
}
