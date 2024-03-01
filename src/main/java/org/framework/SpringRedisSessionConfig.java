package org.framework;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.data.redis.SpringSessionSaveRedisMongoSessionAsSecondary;
import org.springframework.session.data.redis.config.annotation.SpringSessionRedisConnectionFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

@Slf4j
public class SpringRedisSessionConfig {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private String redisNamespace = RedisIndexedSessionRepository.DEFAULT_NAMESPACE;

    private static SpringSessionSaveRedisMongoSessionAsSecondary springSessionSaveRedisMongoSessionAsSecondary;

    private RedisSerializer<Object> defaultRedisSerializer;

    private static RedisIndexedSessionRepository redisIndexedSessionRepository;

    private RedisConnectionFactory redisConnectionFactory;

    public SpringRedisSessionConfig() {}

    public SpringSessionSaveRedisMongoSessionAsSecondary getSpringRedisOperationsSessionRepository() {
        if (springSessionSaveRedisMongoSessionAsSecondary != null) {
            return springSessionSaveRedisMongoSessionAsSecondary;
        }

        RedisIndexedSessionRepository sessionRepository = getRedisOperationsSessionRepository();
        springSessionSaveRedisMongoSessionAsSecondary =
                new SpringSessionSaveRedisMongoSessionAsSecondary(sessionRepository.getSessionRedisOperations());
        return springSessionSaveRedisMongoSessionAsSecondary;
    }

    public RedisIndexedSessionRepository getRedisOperationsSessionRepository() {
        if (redisIndexedSessionRepository != null) {
            return redisIndexedSessionRepository;
        }
        RedisTemplate<String, Object> redisTemplate = createRedisTemplate();
        redisIndexedSessionRepository = new RedisIndexedSessionRepository(redisTemplate);
        if (this.defaultRedisSerializer != null) {
            redisIndexedSessionRepository.setDefaultSerializer(this.defaultRedisSerializer);
        }
        if (StringUtils.hasText(this.redisNamespace)) {
            redisIndexedSessionRepository.setRedisKeyNamespace(this.redisNamespace);
        }
        redisIndexedSessionRepository.setApplicationEventPublisher(this.applicationEventPublisher);
        int database = resolveDatabase();
        redisIndexedSessionRepository.setDatabase(database);
        return redisIndexedSessionRepository;
    }

    @Autowired
    public void setRedisConnectionFactory(
            @SpringSessionRedisConnectionFactory ObjectProvider<RedisConnectionFactory> springSessionRedisConnectionFactory,
            ObjectProvider<RedisConnectionFactory> redisConnectionFactory) {
        RedisConnectionFactory redisConnectionFactoryToUse =
                springSessionRedisConnectionFactory.getIfAvailable();
        if (redisConnectionFactoryToUse == null) {
            redisConnectionFactoryToUse = redisConnectionFactory.getObject();
        }
        this.redisConnectionFactory = redisConnectionFactoryToUse;
    }

    @Autowired(required = false)
    @Qualifier("springSessionDefaultRedisSerializer")
    public void setDefaultRedisSerializer(RedisSerializer<Object> defaultRedisSerializer) {
        this.defaultRedisSerializer = defaultRedisSerializer;
    }

    private RedisTemplate<String, Object> createRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        if (this.defaultRedisSerializer != null) {
            redisTemplate.setDefaultSerializer(this.defaultRedisSerializer);
        }
        redisTemplate.setConnectionFactory(this.redisConnectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    private int resolveDatabase() {
        if (ClassUtils.isPresent("io.lettuce.core.RedisClient", null)
                && this.redisConnectionFactory instanceof LettuceConnectionFactory) {
            return ((LettuceConnectionFactory) this.redisConnectionFactory).getDatabase();
        }
        if (ClassUtils.isPresent("redis.clients.jedis.Jedis", null)
                && this.redisConnectionFactory instanceof JedisConnectionFactory) {
            return ((JedisConnectionFactory) this.redisConnectionFactory).getDatabase();
        }
        return RedisIndexedSessionRepository.DEFAULT_DATABASE;
    }
}
