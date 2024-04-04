package io.basemod.app.repository.review;

import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface ModelledElementCrudRepository<T, ID extends Serializable> {

    public List<T> findAllByModelledElementDetailElementModelledElementLifecycleLifecycleState(String lifecycleState);

}
