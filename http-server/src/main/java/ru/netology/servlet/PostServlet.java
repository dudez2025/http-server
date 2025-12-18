package ru.netology.servlet;

import com.google.gson.Gson;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.config.AnnotationAppConfig;
import ru.netology.controller.PostController;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PostServlet extends HttpServlet {
    private PostController controller;
    private Gson gson;
    
    @Override
    public void init() {
        // Spring Annotation Config
        ApplicationContext context = new AnnotationConfigApplicationContext(AnnotationAppConfig.class);
        controller = context.getBean(PostController.class);
        gson = new Gson();
        System.out.println("PostServlet initialized with Annotation Config");
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.getWriter().print(gson.toJson(controller.all()));
            return;
        }
        
        try {
            long id = Long.parseLong(pathInfo.substring(1));
            Post post = controller.getById(id);
            resp.getWriter().print(gson.toJson(post));
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().print("{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"error\":\"Invalid request\"}");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        try {
            String body = req.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
            Post post = gson.fromJson(body, Post.class);
            Post saved = controller.save(post);
            resp.getWriter().print(gson.toJson(saved));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"error\":\"Invalid request\"}");
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"error\":\"Missing post id\"}");
            return;
        }
        
        try {
            long id = Long.parseLong(pathInfo.substring(1));
            controller.removeById(id);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print("{\"message\":\"Post deleted\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"error\":\"Invalid request\"}");
        }
    }
}