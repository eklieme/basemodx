package io.basemod.app.base.lifecycle;

import io.basemod.app.security.authentication.domain.BaseUser;
import io.basemod.app.domain.DomainElement;

public class ConversationEntry extends DomainElement {

    private String message;
    private String creatorUniqueId;

    private long createdAt;

    public ConversationEntry(String id, String message, String creatorUniqueId, long createdAt) {
        super(id);
        this.message = message;
        this.creatorUniqueId = creatorUniqueId;
        this.createdAt = createdAt;
    }

    public ConversationEntry(String message, String creatorUniqueId, long createdAt) {
        this.message = message;
        this.creatorUniqueId = creatorUniqueId;
        this.createdAt = createdAt;
    }

    public ConversationEntry() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatorUniqueId() {
        return creatorUniqueId;
    }

    public void setCreatorUniqueId(String creatorUniqueId) {
        this.creatorUniqueId = creatorUniqueId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ConversationEntry{" +
                "message='" + message + '\'' +
                ", entryCreator=" + creatorUniqueId +
                ", createdAt=" + createdAt +
                "} " + super.toString();
    }
}
