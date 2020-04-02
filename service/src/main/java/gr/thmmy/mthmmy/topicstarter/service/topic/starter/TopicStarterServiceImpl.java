package gr.thmmy.mthmmy.topicstarter.service.topic.starter;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import gr.thmmy.mthmmy.topicstarter.entity.topic.QTopicStarter;
import gr.thmmy.mthmmy.topicstarter.entity.topic.TopicStarter;
import gr.thmmy.mthmmy.topicstarter.repository.topic.TopicStarterRepository;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
@Data
@Service
public class TopicStarterServiceImpl implements TopicStarterService {

    private final TopicStarterRepository topicStarterRepository;

    @Override
    public Try<Page<TopicStarter>> getWithFilters(final @Nullable String user,
                                                  final @Nullable String board,
                                                  final @Nullable String topic,
                                                  final Pageable pageable) {
        requireNonNull(pageable, "pageable is null");

        return Try
                .of(() -> Expressions.asBoolean(true).isTrue())
                .flatMap(topicStarterPredicate -> Option
                        .of(user)
                        .map(userNotNull -> extractId(userNotNull)
                                .toTry()
                                .flatMap(this::getUserIdPredicate)
                                .orElse(getUsernamePredicate(userNotNull))
                                .map(topicStarterPredicate::and))
                        .getOrElse(Try.success(topicStarterPredicate))
                )
                .flatMap(topicStarterPredicate -> Option
                        .of(board)
                        .map(boardNotNull -> extractId(boardNotNull)
                                .toTry()
                                .flatMap(this::getBoardIdPredicate)
                                .orElse(getBoardTitlePredicate(boardNotNull))
                                .map(topicStarterPredicate::and))
                        .getOrElse(Try.success(topicStarterPredicate))
                )
                .flatMap(topicStarterPredicate -> Option
                        .of(topic)
                        .map(topicNotNull -> extractId(topicNotNull)
                                .toTry()
                                .flatMap(this::getTopicIdPredicate)
                                .orElse(getTopicSubjectPredicate(topicNotNull))
                                .map(topicStarterPredicate::and))
                        .getOrElse(Try.success(topicStarterPredicate))
                )
                .map(topicStarterPredicate -> topicStarterRepository.findAll(topicStarterPredicate, pageable));
    }

    private Option<Long> extractId(final @Nonnull String input) {

        return Try
                .of(() -> Long.parseLong(input))
                .recoverWith(throwable -> Try.success(null))
                .toOption();
    }

    private Try<BooleanExpression> getUsernamePredicate(final @Nonnull String username) {

        return Try
                .of(() -> QTopicStarter.topicStarter)
                .map(qTopicStarter -> qTopicStarter.starterUsername.like(username));
    }

    private Try<BooleanExpression> getUserIdPredicate(final @Nonnull Long userId) {

        return Try
                .of(() -> QTopicStarter.topicStarter)
                .map(qTopicStarter -> qTopicStarter.starterId.eq(userId));
    }

    private Try<BooleanExpression> getBoardTitlePredicate(final @Nonnull String boardTitle) {

        return Try
                .of(() -> QTopicStarter.topicStarter)
                .map(qTopicStarter -> qTopicStarter.boardTitle.like(boardTitle));
    }

    private Try<BooleanExpression> getBoardIdPredicate(final @Nonnull Long boardId) {

        return Try
                .of(() -> QTopicStarter.topicStarter)
                .map(qTopicStarter -> qTopicStarter.boardId.eq(boardId));
    }

    private Try<BooleanExpression> getTopicSubjectPredicate(final @Nonnull String topicSubject) {

        return Try
                .of(() -> QTopicStarter.topicStarter)
                .map(qTopicStarter -> qTopicStarter.topicSubject.like(topicSubject));
    }

    private Try<BooleanExpression> getTopicIdPredicate(final @Nonnull Long topicId) {

        return Try
                .of(() -> QTopicStarter.topicStarter)
                .map(qTopicStarter -> qTopicStarter.topicId.eq(topicId));
    }
}
