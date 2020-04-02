package gr.thmmy.mthmmy.topicstarter.api.controller.topic.starter;

import gr.thmmy.mthmmy.topicstarter.entity.topic.TopicStarter;
import gr.thmmy.mthmmy.topicstarter.service.topic.starter.TopicStarterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class TopicStarterControllerImpl implements TopicStarterController {

    private final TopicStarterService topicStarterService;

    @Override
    @GetMapping("/topicstarters")
    public ResponseEntity<Page<TopicStarter>> topics(@RequestParam(required = false) String user,
                                                     @RequestParam(required = false) String board,
                                                     @RequestParam(required = false) String topic,
                                                     final Pageable pageable) {

        return topicStarterService
                .getWithFilters(user, board, topic, pageable)
                .onFailure(throwable -> log.error("An error has occurred while processing a GET request", throwable))
                .map(ResponseEntity::ok)
                .get();
    }
}
