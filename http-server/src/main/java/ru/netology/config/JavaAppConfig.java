package ru.netology.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;
import ru.netology.controller.PostController;

@Configuration
public class JavaAppConfig {
    
    @Bean
    public PostRepository postRepository() {
        return new PostRepository();
    }
    
    @Bean
    public PostService postService() {
        return new PostService(postRepository());
    }
    
    @Bean
    public PostController postController() {
        return new PostController(postService());
    }
}