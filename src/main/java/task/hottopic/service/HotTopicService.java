package task.hottopic.service;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import task.hottopic.persistence.model.HotTopic;
import task.hottopic.persistence.HotTopicRepository;
import task.hottopic.persistence.model.RssEntry;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Service
public class HotTopicService {

    private final List<String> exclusionWords = Arrays.asList(
            "the", "to", "s", "if", "of", "and", "in", "what", "a", "are", "is",
            "on", "for", "how", "it", "with", "do", "why", "do", "at", "will",
            "year", "new", "you", "us", "", "i", "have", "says", "this", "from",
            "your", "after", "news", "his", "be", "over", "get", "rules", "t", "does", "about",
            "we", "own", "has", "was", "when", "more", "before", "by", "he", "can", "could",
            "they", "their", "as", "than", "claims", "not", "a", "an", "been");

    @Autowired
    private final HotTopicRepository repo;

    public HotTopicService(HotTopicRepository repo) {
        this.repo = repo;
    }

    public List<Long> analyseFeeds(Set<String> urls) {
        Set<RssEntry> feed = fetchFeeds(urls);

        List<String> words = countWords(feed);
        words.removeAll(exclusionWords);

        Map<String, Integer> occurrences = countOccurrences(words);
        Map<String, Integer> sortedByOccur = sortByOccurrences(occurrences);

        List<HotTopic> hotTopics = persistHotTopics(feed, sortedByOccur);

        return hotTopics.stream()
                .map(HotTopic::getId)
                .collect(Collectors.toList());
    }

    private Set<RssEntry> fetchFeeds(Set<String> urls) {
        Set<RssEntry> feed = new HashSet<>();
        for(String url: urls){
            try (XmlReader reader = new XmlReader(new URL(url))) {
                SyndFeed syndFeed = new SyndFeedInput().build(reader);
                for (SyndEntry entry : syndFeed.getEntries()) {
                    RssEntry i = new RssEntry(entry.getTitle(), entry.getUri());
                    feed.add(i);
                }
            } catch (IOException | FeedException e) {
                e.printStackTrace();
            }
        }
        return feed;
    }

    private List<HotTopic> persistHotTopics(Set<RssEntry> feed, Map<String, Integer> sortedByOccur) {
        List<HotTopic> hotTopics = new ArrayList<>();

        for(String hotTopic: sortedByOccur.keySet()){
            hotTopics.add(new HotTopic(
                    Math.abs(new Random().nextLong()),
                    hotTopic,
                    feed.stream().filter(e -> e.getTitle().toLowerCase().contains(hotTopic)).collect(Collectors.toSet()),
                    sortedByOccur.get(hotTopic)));
        }
        repo.saveAll(hotTopics);
        return hotTopics;
    }

    public Optional<HotTopic> getHotTopicsEntries(Long id) {
        return repo.findById(id);
    }

    private LinkedHashMap<String, Integer> sortByOccurrences(Map<String, Integer> occurCount) {
        return occurCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private Map<String, Integer> countOccurrences(List<String> words) {
        return words.parallelStream().
                collect(Collectors.toConcurrentMap(w -> w, w -> 1, Integer::sum));
    }

    private List<String> countWords(Set<RssEntry> feed) {
        return feed.stream()
                .map(RssEntry::getTitle)
                .filter(Pattern.compile("\\d+").asPredicate().negate())
                .flatMap(title -> Arrays.stream(title.toLowerCase().split("\\W+")))
                .collect(Collectors.toList());
    }
}