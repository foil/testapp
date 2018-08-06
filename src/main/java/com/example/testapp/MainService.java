package com.example.testapp;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class MainService {
    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/users";
    private final String userTemplate = "{" +
            "\"username\": \"%s\"," +
            "\"name\": \"%s\"," +
            "\"email\": \"%s\"," +
            "\"password\": \"%s\"" +
            "}";
    private HttpHeaders httpHeaders;

    public MainService() {
        restTemplate = new RestTemplate();
        httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Accept", "application/json");
        httpHeaders.add("Accept-Charset", "UTF-8");
    }

    public Object exec(String... args) {
        final String helpMsg =
                "Usage:\n" +
                "testapp [c|r|u|d|l] args\n" +
                "args has prefix of:\n" +
                "[username:|name:|email:|password:|id:|page:|size:]\n" +
                "for example:\n" +
                "testapp c username:aa name:bb email:a@b.c password:d\n" +
                "will create a user";

        if (args == null || args.length == 0)
            return helpMsg;

        String username = null;
        String name = null;
        String email = null;
        String password = null;
        String id = null;
        Integer page = null;
        Integer size = null;
        for (int i = 1; i < args.length; i ++) {
            if (args[i].startsWith("username:"))
                username = args[i].substring("username:".length());
            else if (args[i].startsWith("name:"))
                name = args[i].substring("name:".length());
            else if (args[i].startsWith("email:"))
                email = args[i].substring("email:".length());
            else if (args[i].startsWith("password:"))
                password = args[i].substring("password:".length());
            else if (args[i].startsWith("id:"))
                id = args[i].substring("id:".length());
            else if (args[i].startsWith("page:"))
                try {
                    page = Integer.valueOf(args[i].substring("page:".length()));
                } catch (Exception e) {
                    page = null;
                }
            else if (args[i].startsWith("size:"))
                try {
                    size = Integer.valueOf(args[i].substring("size:".length()));
                } catch (Exception e) {
                    size = null;
                }
        }

        Object resp;
        switch (args[0]) {
            case "c":
                resp = create(username, name, email, password);
                break;
            case "r":
                resp = read(id);
                break;
            case "u":
                resp = update(id, username, name, email, password);
                break;
            case "d":
                resp = delete(id);
                break;
            case "l":
                resp = list(page, size);
                break;
            default:
                resp = helpMsg;
        }
        return resp;
    }

    private String create(String username, String name, String email, String password) {
        String user = String.format(userTemplate, username, name, email, password);
        HttpEntity<String> requestEntity = new HttpEntity<>(user, httpHeaders);
        ResponseEntity<Object> resp = restTemplate.exchange(baseUrl, HttpMethod.PUT, requestEntity, Object.class);
        return resp.getBody().toString();
    }

    private String read(String id) {
        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Object> resp = restTemplate.exchange(baseUrl + "/" + id, HttpMethod.GET, requestEntity, Object.class);
        return resp.getBody().toString();
    }

    private String update(String id, String username, String name, String email, String password) {
        if (name == null)
            name = "";
        if (email == null)
            email = "";
        if (password == null)
            password = "";
        String user = String.format(userTemplate, username, name, email, password);
        HttpEntity<String> requestEntity = new HttpEntity<>(user, httpHeaders);
        ResponseEntity<Object> resp = restTemplate.postForEntity(baseUrl + "/" + id, requestEntity, Object.class);
        return resp.getBody().toString();
    }

    private String delete(String id) {
        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Object> resp = restTemplate.exchange(baseUrl + "/" + id,
                HttpMethod.DELETE, requestEntity, Object.class);
        return resp.getBody().toString();
    }

    private String[] list(Integer page, Integer size) {
        String url = baseUrl + "?page=" +
                (page == null? "0": page) + "&size=" +
                (size == null? "10": size);
        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Object[]> resp = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Object[].class);

        return Arrays.stream(resp.getBody()).map(Object::toString).toArray(String[]::new);
    }
}