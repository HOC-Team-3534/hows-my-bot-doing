package org.team3534.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import org.team3534.entity.EventTeamEntity;

@ApplicationScoped
public class EventTeamDao {
    @Inject EntityManager em;

    @Transactional
    public void upsert(EventTeamEntity eventTeamEntity) {
        em.merge(eventTeamEntity);
    }

    public void upsert(List<EventTeamEntity> eventTeamEntities) {
        eventTeamEntities.forEach(this::upsert);
    }

    public List<EventTeamEntity> findAll() {
        var query = em.createQuery("SELECT et FROM EventTeamEntity et", EventTeamEntity.class);

        return query.getResultList();
    }
}
