package com.cubastion.net.URLShortsDemo.service;
import com.cubastion.net.URLShortsDemo.convertors.URLConvertorHandler;
import com.cubastion.net.URLShortsDemo.database.URLShortRedisManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class URLServiceHandler {
    private final URLShortRedisManager urlRedisRepo;
    private final URLConvertorHandler urlConvertorHandler;
    private static final Logger logger = Logger.getLogger(URLServiceHandler.class.getName());

    public URLServiceHandler(URLShortRedisManager urlRedisRepo, URLConvertorHandler urlConvertorHandler){
        this.urlRedisRepo = urlRedisRepo;
        this.urlConvertorHandler = urlConvertorHandler;
    }

    public String
    makeShortURL(String localURL, String longURL, HttpServletRequest request){
        try {
            logger.info("Shortening Process Start");
            Long id = this.urlRedisRepo.incrementID();
            String uniqueIDBase64 = this.urlConvertorHandler.createUniqueIdFromLongId(id);
            logger.info("base 10 to base64 conv " + uniqueIDBase64);
            String baseStringFromURL = request.getScheme() + "://" + request.getServerName() +
                    ":" + request.getServerPort() + request.getContextPath() + "/api/v1/service/";
            String shortURL = baseStringFromURL + uniqueIDBase64;
//            System.out.print(longURL);
            this.urlRedisRepo.saveURL(String.valueOf(id), longURL);
            logger.info("URL Saved in Cassandra & Redis");
            return shortURL;
        } catch (Exception e) {
            logger.severe("URL shortening service failed & failed to save in Redis or Cassandra");
            return null;
        }
    }

    private String
    formatLocalURLFromShortener(String localURL){
        String[] addressPart = localURL.split("/");
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i <  addressPart.length - 1; ++i){
            sb.append(addressPart[i]);
        }
        sb.append("/");
        return sb.toString();
    }

    public String
    getLongURLFromID(String uniqueID) {
        Long dictKey = this.urlConvertorHandler.getDictKeysFromBase64String(uniqueID);
        try{
            String longURL = this.urlRedisRepo.getURL(dictKey);
            logger.info("Redis Result " + longURL);
            if(!Objects.equals(longURL, "")){
                logger.info("Converting shortened URL back");
                return longURL;
            }else{
                logger.warning("URL not found with this id, Checking in cassandra");
                return null;
            }
        }catch (Exception e){
            logger.severe("failed to query in redis or cassandra");
            return null;
        }
    }


    public Boolean
    validateURLService(String url){
        final String URL_REGEX = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$";
        final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);
        Matcher mch = URL_PATTERN.matcher(url);
        return mch.matches();
    }
}
