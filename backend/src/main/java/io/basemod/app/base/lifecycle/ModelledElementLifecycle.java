package io.basemod.app.base.lifecycle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ModelledElementLifecycle implements Serializable {

    private LifecycleState lifecycleState;
    private List<ConversationEntry> lifecycleConversation;


    public ModelledElementLifecycle(LifecycleState lifecycleState, List<ConversationEntry> lifecycleConversation) {
        this.lifecycleState = lifecycleState;
        this.lifecycleConversation = lifecycleConversation;
    }

    public ModelledElementLifecycle() {
        this.lifecycleState = LifecycleState.CREATED;
        this.lifecycleConversation = new ArrayList<>();
    }

    public LifecycleState getLifecycleState() {
        return lifecycleState;
    }

    public void setLifecycleState(LifecycleState lifecycleState) {
        this.lifecycleState = lifecycleState;
    }

    public List<ConversationEntry> getLifecycleConversation() {
        return lifecycleConversation;
    }

    public void setLifecycleConversation(List<ConversationEntry> lifecycleConversation) {
        this.lifecycleConversation = lifecycleConversation;
    }
}
