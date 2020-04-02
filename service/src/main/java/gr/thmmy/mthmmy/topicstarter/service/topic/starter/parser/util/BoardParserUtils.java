package gr.thmmy.mthmmy.topicstarter.service.topic.starter.parser.util;

import io.vavr.control.Try;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

public abstract class BoardParserUtils {

    public static Try<Long> extractBoardIdFromUrl(final String url) {
        requireNonNull(url, "url is null");

        return Try.success(".+?board=([0-9]+)")
                .map(regex -> Pattern.compile(regex, Pattern.MULTILINE))
                .map(pattern -> pattern.matcher(url))
                .map(matcher -> Try
                        .of(matcher::find)
                        .filter(aBoolean -> aBoolean)
                        .map(ignored -> Long.parseLong(matcher.group(1)))
                        .getOrElse(-1L)
                );
    }

    public static Try<Elements> parseSubBoards(final Document document) {
        requireNonNull(document, "document is null");

        return Try.of(() -> document
                .select("div.tborder tbody tr.windowbg2 td>b>a[name^=b]"));
    }
}
