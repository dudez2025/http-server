package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 9999;
    private static final int THREAD_POOL_SIZE = 64;
    private static final List<String> VALID_PATHS = List.of(
            "/index.html", "/spring.svg", "/spring.png", "/resources.html",
            "/styles.css", "/app.js", "/links.html", "/forms.html",
            "/classic.html", "/events.html", "/events.js", "/messages"
    );
    private final ExecutorService threadPool;

    public Server() {
        this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    threadPool.submit(() -> handleConnection(socket));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }

    private void handleConnection(Socket socket) {
        try (
                socket;
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())
        ) {
            Request request = parseRequest(in);
            if (request == null) {
                return;
            }
            
            processRequest(request, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Request parseRequest(BufferedReader in) throws IOException {
        String requestLine = in.readLine();
        if (requestLine == null) {
            return null;
        }

        String[] parts = requestLine.split(" ");
        if (parts.length != 3) {
            return null;
        }

        String method = parts[0];
        String fullPath = parts[1];
        String protocol = parts[2];

        return new Request(method, fullPath, protocol);
    }

    private void processRequest(Request request, BufferedOutputStream out) throws IOException {
        String path = request.getPath();
        
        // Проверяем только путь без query параметров
        if (!VALID_PATHS.contains(path)) {
            sendNotFound(out);
            return;
        }

        // Специальная обработка для /messages с query параметрами
        if (path.equals("/messages")) {
            handleMessages(request, out);
            return;
        }

        Path filePath = Path.of(".", "public", path);
        
        if (path.equals("/classic.html")) {
            sendClassicHtml(filePath, out);
        } else {
            sendFile(filePath, out);
        }
    }

    private void handleMessages(Request request, BufferedOutputStream out) throws IOException {
        // Пример обработки query параметров для /messages
        String lastParam = request.getQueryParam("last");
        StringBuilder response = new StringBuilder("<html><body><h1>Messages</h1>");
        
        if (lastParam != null && !lastParam.isEmpty()) {
            try {
                int last = Integer.parseInt(lastParam);
                response.append("<p>Showing last ").append(last).append(" messages</p>");
            } catch (NumberFormatException e) {
                response.append("<p>Invalid 'last' parameter: ").append(lastParam).append("</p>");
            }
        } else {
            response.append("<p>Showing all messages</p>");
        }
        
        // Демонстрация всех query параметров
        response.append("<h2>Query Parameters:</h2><ul>");
        for (Map.Entry<String, List<String>> entry : request.getQueryParams().entrySet()) {
            for (String value : entry.getValue()) {
                response.append("<li>").append(entry.getKey()).append(" = ").append(value).append("</li>");
            }
        }
        response.append("</ul>");
        
        response.append("</body></html>");
        
        byte[] content = response.toString().getBytes(StandardCharsets.UTF_8);
        String headers = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + content.length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";
        
        out.write(headers.getBytes());
        out.write(content);
        out.flush();
    }

    private void sendNotFound(BufferedOutputStream out) throws IOException {
        String response = "HTTP/1.1 404 Not Found\r\n" +
                "Content-Length: 0\r\n" +
                "Connection: close\r\n" +
                "\r\n";
        out.write(response.getBytes());
        out.flush();
    }

    private void sendClassicHtml(Path filePath, BufferedOutputStream out) throws IOException {
        String template = Files.readString(filePath);
        byte[] content = template.replace(
                "{time}",
                LocalDateTime.now().toString()
        ).getBytes();
        
        String mimeType = Files.probeContentType(filePath);
        String headers = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + mimeType + "\r\n" +
                "Content-Length: " + content.length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";
        
        out.write(headers.getBytes());
        out.write(content);
        out.flush();
    }

    private void sendFile(Path filePath, BufferedOutputStream out) throws IOException {
        String mimeType = Files.probeContentType(filePath);
        long length = Files.size(filePath);
        
        String headers = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + mimeType + "\r\n" +
                "Content-Length: " + length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";
        
        out.write(headers.getBytes());
        Files.copy(filePath, out);
        out.flush();
    }

    public void stop() {
        threadPool.shutdown();
    }
}