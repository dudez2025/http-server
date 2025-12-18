package ru.netology;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.config.JavaAppConfig;
import ru.netology.controller.PostController;
import ru.netology.model.Post;

import static org.junit.jupiter.api.Assertions.*;

public class JavaConfigTest {
    
    @Test
    void testJavaConfigDI() {
        ApplicationContext context = new AnnotationConfigApplicationContext(JavaAppConfig.class);
        
        // Проверяем, что все бины созданы
        PostController controller = context.getBean(PostController.class);
        assertNotNull(controller);
        
        // Тестируем CRUD операции
        Post post = new Post();
        post.setContent("Test post from java config");
        
        Post saved = controller.save(post);
        assertNotNull(saved.getId());
        assertEquals("Test post from java config", saved.getContent());
        
        Post retrieved = controller.getById(saved.getId());
        assertEquals(saved.getId(), retrieved.getId());
        
        controller.removeById(saved.getId());
    }
}