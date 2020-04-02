package gr.thmmy.mthmmy.topicstarter.service.topic.starter;

import gr.thmmy.mthmmy.topicstarter.entity.topic.TopicStarter;
import io.vavr.control.Try;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TopicStarterService {

    Try<Page<TopicStarter>> getWithFilters(String user,
                                           String board,
                                           String topic,
                                           Pageable pageable);
}
