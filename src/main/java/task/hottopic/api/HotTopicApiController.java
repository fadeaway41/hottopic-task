package task.hottopic.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import task.hottopic.persistence.model.HotTopic;
import task.hottopic.service.HotTopicService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
public class HotTopicApiController {

    @Autowired
    private HotTopicService hotTopicService;

    @PostMapping(value = "/analyse/new", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<List<Long>> analyseFeeds(@RequestBody Set<String> urls){
        if (urls.size() < 2) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<> (hotTopicService.analyseFeeds(urls), HttpStatus.OK);
    }

    @GetMapping(value = "/frequency/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<Optional<HotTopic>> getHotTopicEntries(@PathVariable Long id){
        if(hotTopicService.getHotTopicsEntries(id).isPresent()) {
            return new ResponseEntity<>(hotTopicService.getHotTopicsEntries(id), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
