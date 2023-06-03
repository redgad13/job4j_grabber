package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;

public class HabrCareerParse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    public static void main(String[] args) throws IOException {
        StringBuilder builder = new StringBuilder(PAGE_LINK);
        Connection connection;
        for (int i = 1; i < 6; i++) {
            connection = Jsoup.connect(builder.toString());
            getPage(connection);
            builder = new StringBuilder(PAGE_LINK).append("?page=").append(i);
        }
    }

    public static void getPage(Connection connection) throws IOException {
        Document document = connection.get();
        Elements rows = document.select(".vacancy-card__inner");
        rows.forEach(row -> {
            Element titleElement = row.select(".vacancy-card__title").first();
            Element linkElement = titleElement.child(0);
            Element date = row.select(".vacancy-card__date").first();
            String vacancyName = titleElement.text();
            String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
            String exactDate = date.child(0).attr("datetime");
            HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
            String formattedTime = parser.parse(exactDate).toString();
            System.out.printf("%s %s %s%n", vacancyName, link, formattedTime);
        });
    }
}