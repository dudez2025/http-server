package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Request {
    private final String method;
    private final String path;
    private final String protocol;
    private final Map<String, List<String>> queryParams;

    public Request(String method, String fullPath, String protocol) {
        this.method = method;
        this.protocol = protocol;
        
        // Разделяем путь и query параметры
        String[] parts = fullPath.split("\\?", 2);
        this.path = parts[0];
        
        if (parts.length > 1) {
            this.queryParams = parseQueryParams(parts[1]);
        } else {
            this.queryParams = new HashMap<>();
        }
    }

    private Map<String, List<String>> parseQueryParams(String queryString) {
        Map<String, List<String>> params = new HashMap<>();
        
        try {
            List<NameValuePair> pairs = URLEncodedUtils.parse(
                new URI("http://localhost?" + queryString), 
                StandardCharsets.UTF_8
            );
            
            for (NameValuePair pair : pairs) {
                params.computeIfAbsent(pair.getName(), k -> new ArrayList<>())
                      .add(pair.getValue());
            }
        } catch (URISyntaxException e) {
            System.err.println("Error parsing query string: " + queryString);
        }
        
        return params;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    /**
     * Получить значение query параметра по имени
     * Возвращает первое значение, если параметров несколько
     */
    public String getQueryParam(String name) {
        List<String> values = queryParams.get(name);
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }

    /**
     * Получить все значения query параметра по имени
     */
    public List<String> getQueryParamMultiple(String name) {
        return queryParams.getOrDefault(name, Collections.emptyList());
    }

    /**
     * Получить все query параметры
     */
    public Map<String, List<String>> getQueryParams() {
        return Collections.unmodifiableMap(queryParams);
    }

    /**
     * Получить полный путь с query параметрами (если были)
     */
    public String getFullPath() {
        if (queryParams.isEmpty()) {
            return path;
        }
        
        StringBuilder sb = new StringBuilder(path);
        sb.append("?");
        
        boolean first = true;
        for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
            for (String value : entry.getValue()) {
                if (!first) {
                    sb.append("&");
                }
                sb.append(entry.getKey()).append("=").append(value);
                first = false;
            }
        }
        
        return sb.toString();
    }
}