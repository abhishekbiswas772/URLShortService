package com.cubastion.net.URLShortsDemo.database;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
public class URLShortRedisManager {
    private final RedisTemplate<String, String> redisTemplate;
    private ValueOperations<String, String> valueOperations;
    private final String idKey = "id";
    private final String urlKeyPrefix = "url:";

    public URLShortRedisManager(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        this.valueOperations = redisTemplate.opsForValue();
    }

    public long incrementID() {
        Long id = this.valueOperations.increment(idKey);
        if (id == null) {
            throw new IllegalStateException("Failed to increment ID");
        }
        return id;
    }

    public void saveURL(String key, String longURL) {
        this.valueOperations.set(urlKeyPrefix + key, longURL);
    }

    public String getURL(Long id) {
        String url = this.valueOperations.get(urlKeyPrefix + id);
        return url != null ? url : "";
    }
}
