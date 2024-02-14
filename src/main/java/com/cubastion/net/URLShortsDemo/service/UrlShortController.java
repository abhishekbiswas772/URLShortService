package com.cubastion.net.URLShortsDemo.service;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.HashMap;
import java.util.logging.Logger;

@RestController
public class UrlShortController {
    private static final Logger logger = Logger.getLogger(UrlShortController.class.getName());
    private final URLServiceHandler urlServiceHandler;

    @Autowired
    public UrlShortController(URLServiceHandler urlServiceHandler){
        this.urlServiceHandler = urlServiceHandler;
    }

    @PostMapping(value = "/api/v1/service/shortener", consumes = {"application/json"})
    public ResponseEntity<?>
    shortingURLHandler(@RequestBody final ShortingServiceModel shortingServiceModel, HttpServletRequest request){
        HashMap<String, Object> response = new HashMap<>();
        try{
            logger.info("Received url to shorten");
            String longURL = shortingServiceModel.getUrl();
            if(this.urlServiceHandler.validateURLService(longURL)){
                String localURL = request.getRequestURL().toString();
                String shortURL = this.urlServiceHandler.makeShortURL(localURL, longURL, request);
                logger.info("Shorting url" + shortURL);
                if(shortURL != null){
                    logger.info("Shorting URL is Done & Saved In Redis");
                    response.put("status", true);
                    response.put("short_url", shortURL);
                    return new ResponseEntity<>(response, HttpStatus.CREATED);
                }else{
                    logger.severe("Shorting Service Failed");
                    response.put("status", false);
                    response.put("message", "Please Send a valid URL");
                    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }else{
                logger.severe("Incorrect URL");
                response.put("status", false);
                response.put("message", "Please Send a valid URL");
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }catch (Exception e){
            logger.severe("Request failed");
            response.put("status", false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/api/v1/service/{id}")
    public ResponseEntity<Object> redirectView(@PathVariable String id) {
        try {
            logger.info("Received shortened URL to redirect: " + id);
            String longURL = this.urlServiceHandler.getLongURLFromID(id);
            if (longURL != null && !longURL.isEmpty()) {
                if (!longURL.startsWith("http://") && !longURL.startsWith("https://")) {
                    longURL = "http://" + longURL; // Ensure URL has a protocol
                }
                // Use ResponseEntity to set headers and status
                HttpHeaders headers = new HttpHeaders();
                headers.setLocation(URI.create(longURL));
                return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
            } else {
                logger.warning("URL not found for ID: " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.severe("Error during URL redirection for ID: " + id + " - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during URL redirection: " + e.getMessage());
        }
    }


}

