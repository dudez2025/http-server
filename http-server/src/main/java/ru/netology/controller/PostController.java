package ru.netology.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.netology.model.Post;
import ru.netology.service.PostService;

import java.util.List;

@Controller
public class PostController {
    private final PostService service;
    
    @Autowired
    public PostController(PostService service) {
        this.service = service;
    }
    
    public List<Post> all() {
        return service.all();
    }
    
    public Post getById(long id) {
        return service.getById(id);
    }
    
    public Post save(Post post) {
        return service.save(post);
    }
    
    public void removeById(long id) {
        service.removeById(id);
    }
}