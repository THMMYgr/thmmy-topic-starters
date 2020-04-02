package gr.thmmy.mthmmy.topicstarter.api;

import gr.thmmy.mthmmy.topicstarter.TopicStarterRepositoryConfig;
import gr.thmmy.mthmmy.topicstarter.entity.TopicStarterEntityConfiguration;
import gr.thmmy.mthmmy.topicstarter.scheduled.TopicStarterSchedulesConfig;
import gr.thmmy.mthmmy.topicstarter.service.TopicStarterServiceConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        TopicStarterServiceConfiguration.class,
        TopicStarterRepositoryConfig.class,
        TopicStarterEntityConfiguration.class,
        TopicStarterSchedulesConfig.class
})
public class TopicStarterApiConfig {
}
