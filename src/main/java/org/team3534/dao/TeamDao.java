package org.team3534.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.experimental.Delegate;

import java.util.List;
import org.team3534.entity.TeamEntity;

@ApplicationScoped
public class TeamDao {
    @Inject
    EntityManager em;

    @Transactional
    public void upsert(TeamEntity teamEntity) {
        em.merge(teamEntity);
    }

    public List<TeamEntity> findByEvent(String eventKey) {
        var query = em.createQuery(
                "SELECT t FROM EventTeamEntity et JOIN TeamEntity t ON et.teamKey = t.key AND et.eventKey = :eventKey",
                TeamEntity.class);

        query.setParameter("eventKey", eventKey);

        return query.getResultList();
    }

    @Data
    public static class TeamWithEventStats {
        @Delegate
        private TeamEntity teamEntity;
        private float oprs, dprs, ccwms, highestOprs;
    }

    public List<TeamWithEventStats> findByEventWithStats(String eventKey) {
        var query = em.createQuery(
                """
                            SELECT t, eo.oprs, eo.ddprs, eo.ccwms, o.highestOprs
                            FROM EventTeamEntity et
                            JOIN TeamEntity t ON et.teamKey = t.key AND et.eventKey = :eventKey
                            LEFT JOIN EventOprsEntity eo ON eo.eventKey = et.eventKey AND eo.teamKey = t.key
                            JOIN (
                                SELECT
                                    etet.teamKey,
                                    MAX(eoeo.oprs) as highestOprs
                                FROM
                                    EventTeamEntity etet
                                    JOIN EventEntity ee ON etet.eventKey = ee.key AND ee.year = et.year
                                    JOIN EventOprsEntity eoeo ON ee.key = eoeo.eventKey
                                GROUP BY etet.teamKey
                            ) o ON o.teamKey = t.key
                        """,
                TeamWithEventStats.class);

        query.setParameter("eventKey", eventKey);

        return query.getResultList();
    }
}
