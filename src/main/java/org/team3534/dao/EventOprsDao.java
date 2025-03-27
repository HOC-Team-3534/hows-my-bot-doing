package org.team3534.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import org.team3534.entity.EventOprsEntity;

@ApplicationScoped
public class EventOprsDao {
    @Inject EntityManager em;

    @Transactional
    public EventOprsEntity upsert(EventOprsEntity eventOprsEntity) {
        return em.merge(eventOprsEntity);
    }

    @Transactional
    public List<EventOprsEntity> upsert(List<EventOprsEntity> eventOprsEntities) {
        return eventOprsEntities.stream().map(em::merge).toList();
    }

    public List<EventOprsEntity> findAll() {
        var query = em.createQuery("SELECT et FROM EventOprsEntity et", EventOprsEntity.class);

        return query.getResultList();
    }
}
