package gr.thmmy.mthmmy.topicstarter.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.util.UUID;

@Data
@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @Column(columnDefinition = "UUID")
    protected String id;

    @PrePersist
    protected void onCreate() {
        this.id = UUID.randomUUID().toString();
    }
}
