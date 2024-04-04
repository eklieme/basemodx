package io.basemod.app.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.basemod.app.security.authentication.domain.BaseUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class NoModellerSerializer extends StdDeserializer<BaseUser> {

    Logger logger = LoggerFactory.getLogger(NoModellerSerializer.class);

    public NoModellerSerializer(Class<?> vc) {
        super(vc);
    }

    public NoModellerSerializer() {
        this(null);
    }


    @Override
    public BaseUser deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {



        if(jp == null) {
            logger.debug("No Modeller provided, return default modeller {}", BaseUser.getDefaultModeller());
            return new BaseUser(
                    BaseUser.getDefaultModeller().getSocialLoginProvider(),
                    BaseUser.getDefaultModeller().getSocialLoginUserId()
            );
        }
        return new BaseUser(false);
    }
}
