package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;

public class HabrCareerParse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }


    public static void main(String[] args) throws IOException {
        StringBuilder builder = new StringBuilder(PAGE_LINK);
        Connection connection;
        for (int i = 2; i < 6; i++) {
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
            DateTimeParser parser = new HabrCareerDateTimeParser();
            String formattedTime = parser.parse(exactDate).toString();
            HabrCareerParse habrCareerParse = new HabrCareerParse(new HabrCareerDateTimeParser());
            String description = null;
            try {
                description = habrCareerParse.retrieveDescription(link);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.printf("%s %s %s%n", vacancyName, link, formattedTime);
            System.out.println(description);
        });
    }

    private String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Element description = document.select(".faded-content__container").first();
        return description.text();
    }
}