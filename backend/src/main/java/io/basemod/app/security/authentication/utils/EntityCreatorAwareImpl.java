package io.basemod.app.security.authentication.utils;

import io.basemod.app.security.authentication.domain.BaseUser;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;


public class EntityCreatorAwareImpl<T> implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(BaseUser.class::isInstance)
                .map(BaseUser.class::cast)
                .map(BaseUser::getUniqueId);
    }
}
