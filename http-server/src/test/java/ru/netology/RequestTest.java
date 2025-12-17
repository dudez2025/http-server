package ru.netology;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RequestTest {
    
    @Test
    void testRequestWithoutQueryParams() {
        Request request = new Request("GET", "/index.html", "HTTP/1.1");
        
        assertEquals("GET", request.getMethod());
        assertEquals("/index.html", request.getPath());
        assertEquals("HTTP/1.1", request.getProtocol());
        assertEquals("/index.html", request.getFullPath());
        assertNull(request.getQueryParam("any"));
        assertTrue(request.getQueryParams().isEmpty());
    }
    
    @Test
    void testRequestWithSingleQueryParam() {
        Request request = new Request("GET", "/messages?last=10", "HTTP/1.1");
        
        assertEquals("/messages", request.getPath());
        assertEquals("10", request.getQueryParam("last"));
        assertEquals("/messages?last=10", request.getFullPath());
        
        Map<String, List<String>> params = request.getQueryParams();
        assertEquals(1, params.size());
        assertTrue(params.containsKey("last"));
        assertEquals(List.of("10"), params.get("last"));
    }
    
    @Test
    void testRequestWithMultipleQueryParams() {
        Request request = new Request("GET", "/messages?last=10&sort=asc&filter=unread", "HTTP/1.1");
        
        assertEquals("/messages", request.getPath());
        assertEquals("10", request.getQueryParam("last"));
        assertEquals("asc", request.getQueryParam("sort"));
        assertEquals("unread", request.getQueryParam("filter"));
        
        Map<String, List<String>> params = request.getQueryParams();
        assertEquals(3, params.size());
        assertEquals(List.of("10"), params.get("last"));
        assertEquals(List.of("asc"), params.get("sort"));
        assertEquals(List.of("unread"), params.get("filter"));
    }
    
    @Test
    void testRequestWithMultipleValuesForSameParam() {
        Request request = new Request("GET", "/messages?name=John&name=Doe&age=30", "HTTP/1.1");
        
        assertEquals("/messages", request.getPath());
        assertEquals("John", request.getQueryParam("name")); // Первое значение
        assertEquals("30", request.getQueryParam("age"));
        
        Map<String, List<String>> params = request.getQueryParams();
        assertEquals(2, params.size());
        assertEquals(List.of("John", "Doe"), params.get("name"));
        assertEquals(List.of("30"), params.get("age"));
    }
    
    @Test
    void testRequestWithSpecialCharacters() {
        Request request = new Request("GET", "/search?q=hello+world&page=1", "HTTP/1.1");
        
        assertEquals("/search", request.getPath());
        assertEquals("hello world", request.getQueryParam("q"));
        assertEquals("1", request.getQueryParam("page"));
    }
    
    @Test
    void testGetQueryParamMultiple() {
        Request request = new Request("GET", "/test?param=value1&param=value2&other=single", "HTTP/1.1");
        
        List<String> paramValues = request.getQueryParamMultiple("param");
        assertEquals(2, paramValues.size());
        assertTrue(paramValues.contains("value1"));
        assertTrue(paramValues.contains("value2"));
        
        List<String> otherValues = request.getQueryParamMultiple("other");
        assertEquals(1, otherValues.size());
        assertEquals("single", otherValues.get(0));
        
        List<String> nonExistent = request.getQueryParamMultiple("nonexistent");
        assertTrue(nonExistent.isEmpty());
    }
    
    @Test
    void testRequestWithEmptyQuery() {
        Request request = new Request("GET", "/messages?", "HTTP/1.1");
        
        assertEquals("/messages", request.getPath());
        assertTrue(request.getQueryParams().isEmpty());
        assertEquals("/messages", request.getFullPath());
    }
}