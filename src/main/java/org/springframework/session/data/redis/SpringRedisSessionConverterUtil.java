package org.springframework.session.data.redis;

import org.framework.SpringSessionData;
import lombok.experimental.UtilityClass;
import org.springframework.session.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@UtilityClass
public class SpringRedisSessionConverterUtil {

    public static SpringSessionData convertToSessionData(Session session) {
        final RedisIndexedSessionRepository.RedisSession redisSession
                = (RedisIndexedSessionRepository.RedisSession) session;
        return SpringSessionData.builder().id(redisSession.getId()).attributes(getAttributes(redisSession))
                .maxInactiveInterval(redisSession.getMaxInactiveInterval())
                .createdMillis(redisSession.getCreationTime().toEpochMilli()).build();
    }

    private static Map<String, Object> getAttributes(RedisIndexedSessionRepository.RedisSession redisSession) {
        Map<String, Object> attributesMap = new HashMap<>();
        Set<String> attributeNames = redisSession.getAttributeNames();
        attributeNames.forEach(attribute -> {
            attributesMap.put(attribute, redisSession.getAttribute(attribute));
        });
        return attributesMap;
    }
}
