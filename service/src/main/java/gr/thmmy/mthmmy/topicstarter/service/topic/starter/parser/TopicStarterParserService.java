package gr.thmmy.mthmmy.topicstarter.service.topic.starter.parser;

import io.vavr.control.Try;

public interface TopicStarterParserService {

    Try<Void> parseTopicStarters();
}
