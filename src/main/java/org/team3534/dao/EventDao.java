package org.team3534.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import org.team3534.entity.EventEntity;

@ApplicationScoped
public class EventDao {
    @Inject EntityManager em;

    @Inject DistrictDao districtDao;

    @Transactional
    public void upsert(EventEntity eventEntity) {
        // Handle the district relationship
        var district = eventEntity.getDistrict();
        if (district != null) districtDao.upsert(district);

        em.merge(eventEntity);
    }

    public EventEntity find(String key) {
        return em.find(EventEntity.class, key);
    }

    public List<EventEntity> findByYear(int year) {
        var query =
                em.createQuery(
                        "SELECT e FROM EventEntity e WHERE e.year = :year", EventEntity.class);
        query.setParameter("year", year);
        return query.getResultList();
    }

    public List<EventEntity> findByTeamAndYear(String teamKey, int year) {
        var query =
                em.createQuery(
                        "SELECT e FROM EventTeamEntity et JOIN EventEntity e ON et.eventKey = e.key WHERE et.teamKey = :teamKey AND e.year = :year",
                        EventEntity.class);

        query.setParameter("teamKey", teamKey);
        query.setParameter("year", year);

        return query.getResultList();
    }
}
