package ru.netology.repository;

import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class PostRepository {
    private final Map<Long, Post> posts = new ConcurrentHashMap<>();
    private final AtomicLong currentId = new AtomicLong(1);
    
    public List<Post> all() {
        return new ArrayList<>(posts.values());
    }
    
    public Optional<Post> getById(long id) {
        return Optional.ofNullable(posts.get(id));
    }
    
    public Post save(Post post) {
        if (post.getId() == 0) {
            long id = currentId.getAndIncrement();
            post.setId(id);
            posts.put(id, post);
        } else {
            if (posts.containsKey(post.getId())) {
                posts.put(post.getId(), post);
            } else {
                return null;
            }
        }
        return post;
    }
    
    public void removeById(long id) {
        posts.remove(id);
    }
}