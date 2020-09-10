package task.hottopic.persistence.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
public class HotTopic {

    @Id @JsonProperty
    private Long id;
    @JsonProperty
    private String topicName;
    @OneToMany(cascade= CascadeType.ALL)
    @JsonProperty
    private Set<RssEntry> topicRssEntries;
    @JsonProperty
    private Integer occurrences;

    public HotTopic() { }

    public HotTopic(Long id, String topicName, Set<RssEntry> topicRssEntries, Integer occurrences) {
        this.id = id;
        this.topicName = topicName;
        this.topicRssEntries = topicRssEntries;
        this.occurrences = occurrences;
    }

    public Long getId() {
        return id;
    }

    public Integer getOccurrences() {
        return occurrences;
    }
}
