package task.hottopic.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task.hottopic.persistence.model.HotTopic;

@Repository
public interface HotTopicRepository extends JpaRepository<HotTopic, Long>{}