package org.springframework.session.data.mongo;

import org.framework.SpringSessionData;
import lombok.experimental.UtilityClass;
import org.springframework.session.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@UtilityClass
public class SpringMongoSessionConverterUtil {
    public static SpringSessionData convertToSessionData(Session session) {
        final MongoSession mongoSession = (MongoSession) session;
        return SpringSessionData.builder().id(mongoSession.getId()).attributes(getAttributes(mongoSession))
                .createdMillis(mongoSession.getCreationTime().toEpochMilli())
                .maxInactiveInterval(mongoSession.getMaxInactiveInterval()).build();
    }

    private static Map<String, Object> getAttributes(MongoSession mongoSession) {
        Map<String, Object> attributesMap = new HashMap<>();
        Set<String> attributeNames = mongoSession.getAttributeNames();
        attributeNames.forEach(attribute -> {
            attributesMap.put(attribute, mongoSession.getAttribute(attribute));
        });
        return attributesMap;
    }
}
