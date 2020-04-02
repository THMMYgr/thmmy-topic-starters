package gr.thmmy.mthmmy.topicstarter.service.topic.starter.parser;

import gr.thmmy.mthmmy.topicstarter.entity.topic.QTopicStarter;
import gr.thmmy.mthmmy.topicstarter.entity.topic.TopicStarter;
import gr.thmmy.mthmmy.topicstarter.repository.topic.TopicStarterRepository;
import gr.thmmy.mthmmy.topicstarter.service.topic.starter.parser.util.TopicParserUtils;
import io.vavr.Tuple;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;

import static gr.thmmy.mthmmy.topicstarter.service.topic.starter.parser.util.BoardParserUtils.extractBoardIdFromUrl;
import static gr.thmmy.mthmmy.topicstarter.service.topic.starter.parser.util.BoardParserUtils.parseSubBoards;
import static java.util.Objects.requireNonNull;

@Service
@Data
@Slf4j
public class TopicStarterParserServiceImpl implements TopicStarterParserService {
    /* Constants */
    private static final String baseUrl = "https://www.thmmy.gr/smf/index.php?action=forum";
    private static final String RECYCLING_BIN_BOARD_ID = "244.0";
    private static final String USERNAME_ENV_VAR = "TOPIC_STARTERS_USERNAME";
    private static final String PASSWORD_ENV_VAR = "TOPIC_STARTERS_PASSWORD";
    private static final HttpUrl loginUrl = HttpUrl.parse("https://www.thmmy.gr/smf/index.php?action=login2");

    private final OkHttpClient client;
    private final TopicStarterRepository topicStarterRepository;

    @Autowired
    private Environment environment;

    @Override
    public Try<Void> parseTopicStarters() {

        return login()
                .flatMap(ignored -> parseBoard(baseUrl));
    }

    private Try<Void> parseBoard(final String url) {
        requireNonNull(url, "url is null");

        return Try.of(Request.Builder::new)
                // Builds and executes request
                .map(requestBuilder -> requestBuilder
                        .url(url)
                        .build())
                .mapTry(request -> client
                        .newCall(request)
                        .execute())
                .flatMap(this::getResponseString)
                .map(Jsoup::parse)
                .flatMap(document -> parseSubBoards(document) // Parses the sub boards
                        .flatMap(subBoards -> Option
                                .of(subBoards)
                                .toTry()
                                .flatMap(subBoardsNotNull -> Try
                                        .run(() -> subBoardsNotNull
                                                .stream()
                                                .filter(subBoard -> !subBoard
                                                        .attr("href")
                                                        .contains("board=" + RECYCLING_BIN_BOARD_ID))
                                                .forEach(subBoard -> parseBoard(subBoard.attr("href")))
                                        )
                                )
                        )
                        // Parses the topics
                        .flatMap(ignored -> parseTopics(document, url))
                );
    }

    private Try<Void> parseTopics(final Document document,
                                  final String boardUrl) {
        requireNonNull(document, "document is null");
        requireNonNull(boardUrl, "boardUrl is null");

        return Try.of(() -> document // Finds the number of pages in this board
                .select("a.navPages")
                .last())
                .map(pageNumber -> Option
                        .of(pageNumber)
                        .map(Element::text)
                        .map(Integer::parseInt)
                        .getOrElse(1))
                .flatMap(numberOfPages -> Try // Parses the board title
                        .of(() -> document
                                .select("div.nav>b>a")
                                .last()
                                .text())
                        .flatMap(boardTitle -> extractBoardIdFromUrl(boardUrl) // Parses topics of the current page
                                .flatMap(boardId -> saveTopics(document, boardTitle, boardUrl, boardId)
                                        .flatMap(ignored -> IntStream // Parses the topics from the rest of the pages
                                                .range(1, numberOfPages)
                                                .boxed()
                                                .map(page -> Try // Builds the URL of the board for each page
                                                        .of(() -> String.join(".",
                                                                boardUrl.substring(0, boardUrl.lastIndexOf(".")),
                                                                String.valueOf(page * 20))
                                                        ).flatMap(pageUrl -> Try
                                                                .of(Request.Builder::new)
                                                                .map(requestBuilder -> requestBuilder
                                                                        .url(pageUrl)
                                                                        .build()
                                                                )
                                                        )
                                                        .mapTry(request -> client
                                                                .newCall(request)
                                                                .execute())
                                                        .flatMap(this::getResponseString)
                                                        .map(Jsoup::parse)
                                                        .flatMap(pageDocument -> saveTopics(
                                                                pageDocument,
                                                                boardTitle,
                                                                boardUrl,
                                                                boardId)))
                                                .collect(List.collector())
                                                .transform(Try::sequence)
                                        )
                                )
                        ).map(ignored -> null));
    }

    private Try<Void> saveTopics(final Document document,
                                 final String boardTitle,
                                 final String boardUrl,
                                 final Long boardId) {
        requireNonNull(document, "document is null");
        requireNonNull(boardTitle, "boardTitle is null");
        requireNonNull(boardUrl, "boardUrl is null");
        requireNonNull(boardId, "boardId is null");

        return Try // Finds this page's topics
                .of(() -> document
                        .select("table.bordercolor tbody>tr:not([class])"))
                .flatMap(topics -> Try
                        .run(() -> topics
                                .forEach(topicRow -> TopicParserUtils
                                        .parseTopic(topicRow, boardTitle, boardUrl, boardId)
                                        .map(topicStarter -> savedTopicStarter(topicStarter)
                                                .flatMap(savedTopicStarter -> updateSavedTopicStarter(savedTopicStarter, topicStarter))
                                                .getOrElse(topicStarter)
                                        )
                                        .map(topicStarterRepository::save)
                                )
                        )
                );
    }

    private Option<TopicStarter> savedTopicStarter(final TopicStarter topicStarter) {
        requireNonNull(topicStarter, "topicStarter is null");

        return Option
                .of(QTopicStarter.topicStarter)
                .map(qTopicStarter -> qTopicStarter.topicId.eq(topicStarter.getTopicId()))
                .map(topicStarterRepository::findOne)
                .flatMap(Option::ofOptional);
    }

    private Option<TopicStarter> updateSavedTopicStarter(final TopicStarter savedTopicStarter, final TopicStarter newTopicStarter) {
        requireNonNull(savedTopicStarter, "savedTopicStarter is null");
        requireNonNull(newTopicStarter, "newTopicStarter is null");

        savedTopicStarter.setTopicId(newTopicStarter.getTopicId());
        savedTopicStarter.setTopicUrl(newTopicStarter.getTopicUrl());
        savedTopicStarter.setStarterUsername(newTopicStarter.getStarterUsername());
        savedTopicStarter.setStarterUrl(newTopicStarter.getStarterUrl());
        savedTopicStarter.setStarterId(newTopicStarter.getStarterId());
        savedTopicStarter.setBoardTitle(newTopicStarter.getBoardTitle());
        savedTopicStarter.setBoardUrl(newTopicStarter.getBoardUrl());
        savedTopicStarter.setBoardId(newTopicStarter.getBoardId());
        savedTopicStarter.setTopicSubject(newTopicStarter.getTopicSubject());
        savedTopicStarter.setNumberOfReplies(newTopicStarter.getNumberOfReplies());
        savedTopicStarter.setNumberOfViews(newTopicStarter.getNumberOfViews());

        return Option.of(savedTopicStarter);
    }

    private Try<Void> login() {

        return Option
                .of(environment.getProperty(USERNAME_ENV_VAR))
                .map(username -> Option
                        .of(environment.getProperty(PASSWORD_ENV_VAR))
                        .map(password -> Tuple.of(username, password))
                        .getOrElseThrow(() -> new RuntimeException("Password is null"))
                ).map(loginSecrets -> Option
                        .of(loginUrl)
                        .map(loginUrlNotNull -> Try
                                .of(FormBody.Builder::new)
                                .map(builder -> builder
                                        .add("user", loginSecrets._1)
                                        .add("passwrd", loginSecrets._2)
                                        .add("cookielength", "-1") // -1 is forever
                                        .build()
                                ).flatMap(formBody -> Try
                                        .of(Request.Builder::new)
                                        .map(builder -> builder
                                                .url(loginUrlNotNull)
                                                .post(formBody)
                                                .build()
                                        )
                                ).mapTry(request -> client
                                        .newCall(request)
                                        .execute()
                                ).flatMap(response -> Try
                                        .run(() -> response
                                                .body()
                                                .close()
                                        )
                                )
                        ).getOrElseThrow(() -> new RuntimeException("Login URL is null."))
                ).getOrElseThrow(() -> new RuntimeException("Username is null"));
    }

    private Try<String> getResponseString(final Response response) {
        requireNonNull(response, "response is null");

        // Checks response for null and closes response body
        return Try
                .of(response::body)
                .flatMap(responseBody -> Option
                        .of(responseBody)
                        .toTry()
                        .mapTry(ResponseBody::string)
                        .flatMap(responseBodyString -> Try
                                .run(responseBody::close)
                                .map(ignored -> responseBodyString))
                );
    }
}
