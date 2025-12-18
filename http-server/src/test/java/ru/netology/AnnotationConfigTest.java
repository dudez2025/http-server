package ru.netology;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.config.AnnotationAppConfig;
import ru.netology.controller.PostController;
import ru.netology.model.Post;

import static org.junit.jupiter.api.Assertions.*;

public class AnnotationConfigTest {
    
    @Test
    void testAnnotationConfigDI() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AnnotationAppConfig.class);
        
        // Проверяем, что все бины созданы
        PostController controller = context.getBean(PostController.class);
        assertNotNull(controller);
        
        // Тестируем CRUD операции
        Post post = new Post();
        post.setContent("Test post from annotation config");
        
        Post saved = controller.save(post);
        assertNotNull(saved.getId());
        assertEquals("Test post from annotation config", saved.getContent());
        
        Post retrieved = controller.getById(saved.getId());
        assertEquals(saved.getId(), retrieved.getId());
        
        controller.removeById(saved.getId());
    }
}