package org.framework;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.session.data.mongo.AbstractMongoSessionConverter;
import org.springframework.session.data.mongo.JdkMongoSessionConverter;
import org.springframework.session.data.mongo.MongoIndexedSessionRepository;
import org.springframework.session.data.mongo.SpringSessionSaveMongoSessionAsSecondary;
import org.springframework.util.StringUtils;

import java.time.Duration;

@Slf4j
public class SpringMongoSessionConfig {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private String collectionName = "sessions";

    private MongoOperations mongoOperations;

    private SpringSessionSaveMongoSessionAsSecondary springSessionSaveMongoSessionAsSecondary;

    private MongoIndexedSessionRepository mongoIndexedSessionRepository;

    private final Integer MAX_TIME_INACTIVE_SESSION = 1800;

    public SpringMongoSessionConfig(MongoOperations mongoOperations) {
        this.mongoOperations=mongoOperations;
    }

    @Setter
    private AbstractMongoSessionConverter mongoSessionConverter =
            new JdkMongoSessionConverter(Duration.ofSeconds(MAX_TIME_INACTIVE_SESSION));

    public SpringSessionSaveMongoSessionAsSecondary getSpringMongoOperationsSessionRepository() {
        if (springSessionSaveMongoSessionAsSecondary != null) {
            return springSessionSaveMongoSessionAsSecondary;
        }
        SpringSessionSaveMongoSessionAsSecondary springSessionSaveMongoSessionAsSecondary
                = new SpringSessionSaveMongoSessionAsSecondary(mongoOperations);
        setMongoRepositoryParameters(springSessionSaveMongoSessionAsSecondary);
        return springSessionSaveMongoSessionAsSecondary;
    }

    public MongoIndexedSessionRepository getMongoOperationsSessionRepository() {
        if (mongoIndexedSessionRepository != null) {
            return mongoIndexedSessionRepository;
        }
        mongoIndexedSessionRepository =
                new MongoIndexedSessionRepository(mongoOperations);
        setMongoRepositoryParameters(mongoIndexedSessionRepository);
        return mongoIndexedSessionRepository;
    }

    private void setMongoRepositoryParameters(MongoIndexedSessionRepository repository) {
        repository.setMaxInactiveIntervalInSeconds(MAX_TIME_INACTIVE_SESSION);

        if (this.mongoSessionConverter != null) {
            repository.setMongoSessionConverter(this.mongoSessionConverter);
        } else {
            repository.setMongoSessionConverter(mongoSessionConverter);
        }

        if (StringUtils.hasText(this.collectionName)) {
            repository.setCollectionName(this.collectionName);
        }
        repository.setApplicationEventPublisher(this.applicationEventPublisher);
    }
}
