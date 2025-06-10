package com.viettel.spring.cloud.server.config.cache;

import java.time.Duration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.viettel.spring.cloud.server.security.SimpleGrantedAuthorityMixin;

@Configuration
@EnableCaching
public class RedisCacheConfig {
    // @Bean
    // public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
    //     RedisSerializationContext.SerializationPair<Object> jsonSerializer =
    //         RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer());

    //     RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
    //         .entryTtl(Duration.ofMinutes(5))
    //         .serializeValuesWith(jsonSerializer) // 👈 áp dụng ở đây
    //         .disableCachingNullValues();

    //     return RedisCacheManager.builder(redisConnectionFactory)
    //         .cacheDefaults(config)
    //         .build();
    // }
    @Bean
    public RedisSerializer<Object> springSecurityRedisSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // ✅ hỗ trợ LocalDateTime
        mapper.activateDefaultTyping(
            mapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL // Bắt buộc thêm kiểu
        );
        mapper.addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixin.class);
        return new GenericJackson2JsonRedisSerializer(mapper);
    }


    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(5))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(springSecurityRedisSerializer()))
            ;

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .build();
    }
}
