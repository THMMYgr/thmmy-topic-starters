package gr.thmmy.mthmmy.topicstarter.api.controller.topic.starter;

import gr.thmmy.mthmmy.topicstarter.entity.topic.TopicStarter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface TopicStarterController {

    ResponseEntity<Page<TopicStarter>> topics(String user,
                                              String board,
                                              String topic,
                                              Pageable pageable);
}
