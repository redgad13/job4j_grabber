package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    private final DateTimeParser dateTimeParser;

    private int postID = 0;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> posts = new ArrayList<>();
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Elements rows = document.select(".vacancy-card__inner");
        for (Element row : rows) {
            posts.add(createPost(row));
        }
        return posts;
    }

    public static void main(String[] args) throws IOException {
        StringBuilder builder = new StringBuilder(PAGE_LINK);
        HabrCareerParse habrCareerParse = new HabrCareerParse(new HabrCareerDateTimeParser());
        List<Post> posts = new ArrayList<>();
        for (int i = 2; i < 7; i++) {
            posts.addAll(habrCareerParse.list(builder.toString()));
            builder.append("?page=").append(i);
        }
        for (Post post : posts) {
            System.out.println(post);
        }
    }

    private String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Element description = document.select(".faded-content__container").first();
        return description.text();
    }

    private Post createPost(Element row) {
        Post post = null;
        Element titleElement = row.select(".vacancy-card__title").first();
        Element linkElement = titleElement.child(0);
        Element date = row.select(".vacancy-card__date").first();
        String vacancyName = titleElement.text();
        String innerLink = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        String exactDate = date.child(0).attr("datetime");
        String description;
        try {
            description = this.retrieveDescription(innerLink);
            post = new Post(postID++, vacancyName, innerLink, description, dateTimeParser.parse(exactDate));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return post;
    }
}