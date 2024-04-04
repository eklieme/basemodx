package io.basemod.app.characteristic;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.data.rest.core.annotation.RestResource;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ContinuousUserActivity.class, name = UserActivity.CONTINUOUS_TYPE),
        @JsonSubTypes.Type(value = TransitionalUserActivity.class, name = UserActivity.TRANSITIONAL_TYPE) }
)
@RestResource(path = "userActivity")
public abstract class UserActivity implements Serializable {

    public static final String CONTINUOUS_TYPE = "continuous";
    public static final String TRANSITIONAL_TYPE = "transitional";

    private String name;

    public UserActivity(String name) {
        this.name = name;
    }

    public UserActivity() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract String getType();

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        UserActivity that = (UserActivity) other;
        return name.equalsIgnoreCase(that.name);
    }
}
