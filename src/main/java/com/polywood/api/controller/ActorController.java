package com.polywood.api.controller;

import com.polywood.api.auth.Authenticator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@CrossOrigin
@RequestMapping("/actors")
public class ActorController {
    private final String ACTOR_SERVICE_HOST = "localhost";
    private final int ACTOR_SERVICE_PORT = 8082;
    private final String ACTOR_SERVICE_URL = "http://" + ACTOR_SERVICE_HOST + ":" + ACTOR_SERVICE_PORT + "/actors/";


    @RequestMapping(value = "", method = GET)
    @ResponseBody
    public ResponseEntity<String> findAllActorsPaged(
            @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, @RequestParam("sort") Optional<String> sort, @RequestHeader("Authorization") String token) {

        try {
            Authenticator.verifyAndDecodeToken(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }

        RestTemplate restTemplate = new RestTemplate();
        String url = ACTOR_SERVICE_URL + "?" +
                "page=" + page.orElse(0) +
                "&size=" + size.orElse(Integer.MAX_VALUE) +
                "&sort=" + sort.orElse("name");

        return restTemplate.getForEntity(
                URI.create(url), String.class);

    }
    
    @RequestMapping(value = "/maxPage/{size}", method = GET)
    public ResponseEntity<String> getAllActorsPages(@PathVariable("size") Optional<Integer> size, @RequestParam("search") Optional<String> search, @RequestHeader("Authorization") String token) {
        try {
            Authenticator.verifyAndDecodeToken(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
        
        RestTemplate restTemplate = new RestTemplate();
        String url = ACTOR_SERVICE_URL + "maxPage/" + size.orElse(Integer.MAX_VALUE);
        if (search.isPresent())
            url += "?search=" + search.get().replace("+"," ");
        
        return restTemplate.getForEntity(
                URI.create(url), String.class);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getActorById(@PathVariable(value = "id") String id, @RequestHeader("Authorization") String token) {

        try {
            Authenticator.verifyAndDecodeToken(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }

        RestTemplate restTemplate = new RestTemplate();
        String url = ACTOR_SERVICE_URL + id;

        return restTemplate.getForEntity(
                URI.create(url), String.class);
    }

    @GetMapping("/filmography/{id}")
    public ResponseEntity<String> getFilmographyById(@PathVariable(value = "id") String id, @RequestHeader("Authorization") String token) {

        try {
            Authenticator.verifyAndDecodeToken(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }

        RestTemplate restTemplate = new RestTemplate();
        String url = ACTOR_SERVICE_URL + "filmography/" + id;

        return restTemplate.getForEntity(
                URI.create(url), String.class);
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<String> getActorsByName(@PathVariable(value = "name") String name,
                                                   @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, @RequestParam("sort") Optional<String> sort, @RequestHeader("Authorization") String token) {
        
        try {
            Authenticator.verifyAndDecodeToken(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
        
        RestTemplate restTemplate = new RestTemplate();
        String url = null;
        try {
            url = ACTOR_SERVICE_URL + "name/" + URLEncoder.encode(name, "UTF-8") + "?" +
                    "page=" + page.orElse(0) +
                    "&size=" + size.orElse(Integer.MAX_VALUE) +
                    "&sort=" + sort.orElse("name");
        } catch (UnsupportedEncodingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong parameters format");
        }
        
        return restTemplate.getForEntity(
                URI.create(url), String.class);
    }
}
