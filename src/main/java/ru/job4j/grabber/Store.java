package ru.job4j.grabber;

import java.util.List;

public interface Store extends AutoCloseable {
    Post save(Post post);

    List<Post> getAll();

    Post findBy(int id);
}
