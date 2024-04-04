package io.basemod.app.security.authentication.domain;

import io.basemod.app.security.authentication.social.SocialLoginUserInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class BaseUserController {

    private MongoTemplate mongoTemplate;

    private static Logger logger = LoggerFactory.getLogger(BaseUserController.class);

    @Autowired
    public BaseUserController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public BaseUser getBaseUserUsingSocialLoginInformation(SocialLoginUserInformation socialLoginUserInformation) {

        String uniqueId = socialLoginUserInformation.inferUniqueId();
        logger.debug("User logged in with unique id {}, query for user", uniqueId);
        List<BaseUser> baseUsers = getBaseUsersUsingUniqueId(uniqueId);
        logger.debug("\t...found {} users matching unique ID", baseUsers.size());

        if(baseUsers.size()==1) {
            logger.debug("\t... user {} exists already, update last login", baseUsers.get(0));

            baseUsers.get(0).setLastLogin(Instant.now());
            mongoTemplate.save(baseUsers.get(0));
            return baseUsers.get(0);

        } else if (baseUsers.size()==0) {
            logger.debug("\t...user with unique id {}", uniqueId);
            BaseUser newBaseUser = new BaseUser(socialLoginUserInformation.getSocialLoginUserId(),
                    socialLoginUserInformation.getSocialLoginProvider());
            newBaseUser.setLastLogin(Instant.now());
            mongoTemplate.save(newBaseUser);
            logger.debug("\t...persisted user {}", newBaseUser);
            return newBaseUser;
        } else {
            logger.error("Found more than one user matching UNIQUE?!?!? ID {}, returning first found", uniqueId);

            baseUsers.get(0).setLastLogin(Instant.now());
            mongoTemplate.save(baseUsers.get(0));
            return baseUsers.get(0);
        }

    }

    public List<BaseUser> getBaseUsersUsingUniqueId(String uniqueId) {
        Query userByUniqueIdQuery = new Query();
        userByUniqueIdQuery.addCriteria(Criteria.where("uniqueId").is(uniqueId));
        return mongoTemplate.find(userByUniqueIdQuery,BaseUser.class);
    }

    public BaseUser getBaseUserUsingUniqueId(String uniqueId) {
        Query userByUniqueIdQuery = new Query();
        userByUniqueIdQuery.addCriteria(Criteria.where("uniqueId").is(uniqueId));
        return mongoTemplate.findOne(userByUniqueIdQuery,BaseUser.class);
    }

    public void saveBaseUser(BaseUser baseUserToSave) {
        mongoTemplate.save(baseUserToSave);
    }
}
