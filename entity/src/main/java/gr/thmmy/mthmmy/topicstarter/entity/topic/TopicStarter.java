package gr.thmmy.mthmmy.topicstarter.entity.topic;

import gr.thmmy.mthmmy.topicstarter.entity.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "topic_starter")
public class TopicStarter extends AbstractEntity {

    @Column(nullable = false)
    private Long topicId;

    @Column(nullable = false)
    private String topicUrl;

    @Column(nullable = false)
    private String starterUsername;

    private String starterUrl;

    private Long starterId;

    @Column(nullable = false)
    private String boardTitle;

    @Column(nullable = false)
    private String boardUrl;

    @Column(nullable = false)
    private Long boardId;

    @Column(nullable = false)
    private String topicSubject;

    @Column(nullable = false)
    private Long numberOfReplies;

    @Column(nullable = false)
    private Long numberOfViews;
}
