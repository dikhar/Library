package org.framework;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.SpringHttpSessionConfiguration;
import org.springframework.session.data.redis.MultiSessionRepository;
import org.springframework.session.web.http.SessionRepositoryFilter;

@Configuration
public class SpringSessionConfigurator extends SpringHttpSessionConfiguration {

    @Autowired
    private MongoOperations mongoOperations;

    @Bean
    public <S extends Session> SessionRepositoryFilter
            <? extends Session> springSessionRepositoryFilter
            (SessionRepository<S> sessionRepository) {

        SessionRepositoryFilter sessionRepositoryFilter =
                new SessionRepositoryFilter(sessionRepository());
        return sessionRepositoryFilter;
    }

    @Bean
    public SessionRepository sessionRepository() {
        MultiSessionRepository multiSessionRepository = new MultiSessionRepository();
        multiSessionRepository.setSpringMongoSessionConfigs(springMongoSessionConfig());
        multiSessionRepository.setRedisSessionConfig(springRedisSessionConfig());
        return multiSessionRepository;
    }

    @Bean
    public SpringRedisSessionConfig springRedisSessionConfig() {
        return new SpringRedisSessionConfig();
    }

    @Bean
    public SpringMongoSessionConfig springMongoSessionConfig() {
        return new SpringMongoSessionConfig(mongoOperations);
    }
}
