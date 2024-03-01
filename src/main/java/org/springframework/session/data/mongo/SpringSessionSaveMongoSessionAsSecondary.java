package org.springframework.session.data.mongo;


import org.framework.SpringSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.session.data.ISpringSessionOperation;

import java.util.Map;

@Slf4j
public class SpringSessionSaveMongoSessionAsSecondary extends MongoIndexedSessionRepository
        implements ISpringSessionOperation {
    public SpringSessionSaveMongoSessionAsSecondary(MongoOperations mongoOperations) {
        super(mongoOperations);
    }

    @Override
    public void saveAsSecondary(SpringSessionData springSessionData) {
        log.debug("Going to save session as secondary in mongo");
        log.info("Going to save session as secondary in mongo");
        MongoSession mongoSession = findById(springSessionData.getId());
        if(mongoSession==null){
            mongoSession = new MongoSession(springSessionData.getId(),
                    springSessionData.getMaxInactiveInterval().getSeconds());
        }else{
            mongoSession.setMaxInactiveInterval(springSessionData.getMaxInactiveInterval());
        }
        Map<String,Object> keys = springSessionData.getAttributes();
        for(Map.Entry<String,Object> key : keys.entrySet()){
            mongoSession.setAttribute(key.getKey(), key.getValue());
        }
        mongoSession.setCreationTime(springSessionData.getCreatedMillis());
        log.debug("Session created as secondary in mongo");
        log.info("Session created as secondary in mongo");
        super.save(mongoSession);
    }
}