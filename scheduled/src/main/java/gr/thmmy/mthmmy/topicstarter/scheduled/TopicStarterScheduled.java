package gr.thmmy.mthmmy.topicstarter.scheduled;

import gr.thmmy.mthmmy.topicstarter.service.topic.starter.parser.TopicStarterParserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Data
@Slf4j
@Component
public class TopicStarterScheduled {

    private final TopicStarterParserService topicStarterParserService;

    // Runs at 02:00am every day of every month
    @Scheduled(cron = "0 0 02 * * *")
    public void run() throws Exception {

        topicStarterParserService
                .parseTopicStarters()
                .onFailure(throwable -> log.error("An error has occurred while processing a GET request", throwable))
                .get();
    }

    @PostConstruct
    public void init() {

        topicStarterParserService
                .parseTopicStarters()
                .onFailure(throwable -> log.error("An error has occurred while processing a GET request", throwable))
                .get();
    }
}
