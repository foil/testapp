package com.example.testapp.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
public class MainController {
    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/users";
    private final String userTemplate = "{" +
            "\"username\": \"%s\"," +
            "\"name\": \"%s\"," +
            "\"email\": \"%s\"," +
            "\"password\": \"%s\"" +
            "}";
    private HttpHeaders httpHeaders;

    @Autowired
    public MainController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Accept", "application/json");
        httpHeaders.add("Accept-Charset", "UTF-8");
    }

    //  for simplicity, just use get here
    @GetMapping("/create")
    public String create(@RequestParam String username,
                         @RequestParam String name,
                         @RequestParam String email,
                         @RequestParam String password) {
        String user = String.format(userTemplate, username, name, email, password);
        HttpEntity<String> requestEntity = new HttpEntity<>(user, httpHeaders);
        ResponseEntity<String> resp = restTemplate.exchange(baseUrl, HttpMethod.PUT, requestEntity, String.class);
        return resp.getBody();
    }

    @GetMapping("/read")
    public String read(@RequestParam String id) {
        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> resp = restTemplate.exchange(baseUrl + "/" + id, HttpMethod.GET, requestEntity, String.class);
        return resp.getBody();
    }

    @GetMapping("/update")
    public String update(@RequestParam String id,
                         @RequestParam(required = false) String username,
                         @RequestParam(required = false) String name,
                         @RequestParam(required = false) String email,
                         @RequestParam(required = false) String password) {
        if (name == null)
            name = "";
        if (email == null)
            email = "";
        if (password == null)
            password = "";
        String user = String.format(userTemplate, username, name, email, password);
        HttpEntity<String> requestEntity = new HttpEntity<>(user, httpHeaders);
        ResponseEntity<String> resp = restTemplate.postForEntity(baseUrl + "/" + id, requestEntity, String.class);
        return resp.getBody();
    }

    @GetMapping("delete")
    public String delete(@RequestParam String id) {
        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> resp = restTemplate.exchange(baseUrl + "/" + id,
                HttpMethod.DELETE, requestEntity, String.class);
        return resp.getBody();
    }

    @GetMapping("list")
    public String[] list(@RequestParam(required = false) Integer page,
                       @RequestParam(required = false) Integer size) {
        String url = baseUrl;
        if (page != null)
            url += "?page=" + page;
        if (size != null)
            url += "&size=" + size;
        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Object[]> resp = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Object[].class);

        return Arrays.stream(resp.getBody()).map(Object::toString).toArray(String[]::new);
    }
}