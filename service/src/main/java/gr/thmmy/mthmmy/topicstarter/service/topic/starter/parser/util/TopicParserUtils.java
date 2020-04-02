package gr.thmmy.mthmmy.topicstarter.service.topic.starter.parser.util;

import gr.thmmy.mthmmy.topicstarter.entity.topic.TopicStarter;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

public abstract class TopicParserUtils {

    public static Try<TopicStarter> parseTopic(final Element topicRow,
                                               final String boardTitle,
                                               final String boardUrl,
                                               final Long boardId) {
        requireNonNull(topicRow, "topicRow is null");
        requireNonNull(boardTitle, "boardTitle is null");
        requireNonNull(boardUrl, "boardUrl is null");
        requireNonNull(boardId, "boardId is null");

        return Try.of(() -> topicRow
                .select("td"))
                .flatMap(topicColumns -> Try
                        .of(() -> topicColumns
                                .get(3)
                                .select("a")
                                .first())
                        .flatMap(starterUrlElement -> Try
                                .of(TopicStarter::new)
                                .flatMap(topic -> parseTopicSubject(topicColumns)
                                        .map(topicSubject -> {
                                            topic.setTopicSubject(topicSubject);

                                            return topic;
                                        })
                                ).flatMap(topic -> parseTopicUrl(topicColumns)
                                        .flatMap(topicUrl -> extractTopicIdFromUrl(topicUrl)
                                                .map(topicId -> {
                                                    topic.setTopicUrl(topicUrl);
                                                    topic.setTopicId(topicId);

                                                    return topic;
                                                })
                                        )
                                ).flatMap(topic -> parseTopicStarterUsername(topicColumns)
                                        .map(starterUsername -> {
                                            topic.setStarterUsername(starterUsername);

                                            return topic;
                                        })
                                ).flatMap(topic -> parseTopicStarterUrl(starterUrlElement)
                                        .flatMap(topicStarterUrl -> extractTopicStarterIdFromUrl(topicStarterUrl)
                                                .map(topicStarterId -> {
                                                    topic.setStarterUrl(topicStarterUrl);
                                                    topic.setStarterId(topicStarterId);

                                                    return topic;
                                                })
                                        )
                                ).flatMap(topic -> parseTopicNumberOfReplies(topicColumns)
                                        .map(numReplies -> {
                                            topic.setNumberOfReplies(numReplies);

                                            return topic;
                                        })
                                ).flatMap(topic -> parseTopicNumberOfViews(topicColumns)
                                        .map(numViews -> {
                                            topic.setNumberOfViews(numViews);

                                            return topic;
                                        })
                                ).map(topic -> {
                                            topic.setBoardTitle(boardTitle);
                                            topic.setBoardId(boardId);
                                            topic.setBoardUrl(boardUrl);

                                            return topic;
                                        }
                                )
                        )
                );
    }

    private static Try<String> parseTopicSubject(final Elements topicColumns) {
        requireNonNull(topicColumns, "topicColumns is null");

        return Try
                .of(() -> topicColumns
                        .get(2)
                        .select("span>a")
                        .first()
                        .text());
    }

    private static Try<String> parseTopicUrl(final Elements topicColumns) {
        requireNonNull(topicColumns, "topicColumns is null");

        return Try
                .of(() -> topicColumns
                        .get(2)
                        .select("span>a")
                        .first()
                        .attr("href"));
    }

    private static Try<String> parseTopicStarterUsername(final Elements topicColumns) {
        requireNonNull(topicColumns, "topicColumns is null");

        return Try
                .of(() -> topicColumns
                        .get(3)
                        .text());
    }

    private static Try<String> parseTopicStarterUrl(final Element starterUrlEl) {
        requireNonNull(starterUrlEl, "starterUrlEl is null");

        return Try.of(() -> Option
                .of(starterUrlEl)
                .map(starterUrlElNotNull -> starterUrlElNotNull
                        .attr("href"))
                .getOrElse(() -> null));
    }

    private static Try<Long> parseTopicNumberOfReplies(final Elements topicColumns) {
        requireNonNull(topicColumns, "topicColumns is null");

        return Try
                .of(() -> topicColumns
                        .get(4)
                        .text())
                .map(Long::parseLong);
    }

    private static Try<Long> parseTopicNumberOfViews(final Elements topicColumns) {
        requireNonNull(topicColumns, "topicColumns is null");

        return Try
                .of(() -> topicColumns
                        .get(5)
                        .text())
                .map(Long::parseLong);
    }

    private static Try<Long> extractTopicIdFromUrl(final String topicUrl) {
        requireNonNull(topicUrl, "topicUrl is null");

        return Try.success(".+?topic=([0-9]+)")
                .map(regex -> Pattern.compile(regex, Pattern.MULTILINE))
                .map(pattern -> pattern.matcher(topicUrl))
                .map(matcher -> Try.of(matcher::find)
                        .filter(aBoolean -> aBoolean)
                        .map(ignored -> Long.parseLong(matcher.group(1)))
                        .getOrElse(-1L)
                );

    }

    private static Try<Long> extractTopicStarterIdFromUrl(final @Nullable String topicStarterUrl) {

        return Option
                .of(topicStarterUrl)
                .map(topicStarterUrlNotNull -> Try
                        .of(() -> ".+?profile;u=([0-9]+)")
                        .map(regex -> Pattern.compile(regex, Pattern.MULTILINE))
                        .map(pattern -> pattern.matcher(topicStarterUrlNotNull))
                        .map(matcher -> Try.of(matcher::find)
                                .filter(aBoolean -> aBoolean)
                                .map(ignored -> Long.parseLong(matcher.group(1)))
                                .getOrElse(-1L))
                ).getOrElse(Try.success(-1L));
    }
}
