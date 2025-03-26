package org.team3534.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Delegate;
import org.team3534.entity.EventOprsEntity;
import org.team3534.entity.EventTeamEntity;
import org.team3534.entity.TeamEntity;

@ApplicationScoped
public class TeamDao {
    @Inject EntityManager em;

    @Transactional
    public void upsert(TeamEntity teamEntity) {
        em.merge(teamEntity);
    }

    public TeamEntity find(String key) {
        return em.find(TeamEntity.class, key);
    }

    // public List<TeamEntity> findByDistrict(String districtKey) {
    // CriteriaBuilder cb = em.getCriteriaBuilder();

    // CriteriaQuery<TeamEntity> cq = cb.createQuery(TeamEntity.class);

    // return query.getResultList();
    // }

    @Data
    @AllArgsConstructor
    public static class TeamWithEventStats {
        @Delegate private TeamEntity teamEntity;
        private TeamEventOprs teamEventOprs;
        private float highestOprs;
    }

    @Data
    @AllArgsConstructor
    public static class TeamEventOprs {
        private float oprs, dprs, ccwms;
    }

    public List<TeamWithEventStats> findByEventWithStats(String eventKey) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TeamWithEventStats> cq = cb.createQuery(TeamWithEventStats.class);

        // Root from EventTeamEntity, which links events and teams.
        Root<EventTeamEntity> etRoot = cq.from(EventTeamEntity.class);
        // Join to the associated TeamEntity via the mapped "team" property.
        Join<EventTeamEntity, TeamEntity> teamJoin = etRoot.join("team");

        // Use separate roots for EventOprsEntity:
        // eoCurrent: the current event's OPRS record for this team.
        Root<EventOprsEntity> eoCurrent = cq.from(EventOprsEntity.class);
        // eoMax: used to compute the maximum oprs for this team in the event's year.
        Root<EventOprsEntity> eoMax = cq.from(EventOprsEntity.class);

        // WHERE conditions:
        // 1. Filter EventTeamEntity by event key.
        // 2. Match eoCurrent record to the current event and team.
        // 3. For eoMax, match the team and event year.
        cq.where(
                cb.equal(etRoot.get("event").get("key"), eventKey),
                cb.equal(eoCurrent.get("event").get("key"), etRoot.get("event").get("key")),
                cb.equal(eoCurrent.get("team").get("key"), teamJoin.get("key")),
                cb.equal(eoMax.get("team").get("key"), teamJoin.get("key")),
                cb.equal(eoMax.get("event").get("year"), etRoot.get("event").get("year")));

        // Group by team and the current record’s OPRS fields.
        cq.groupBy(teamJoin, eoCurrent.get("oprs"), eoCurrent.get("ddprs"), eoCurrent.get("ccwms"));

        // Construct the DTO:
        // - Build a TeamEventOprs from eoCurrent fields.
        // - Compute highestOprs as MAX(eoMax.oprs).
        cq.select(
                cb.construct(
                        TeamWithEventStats.class,
                        teamJoin, // TeamEntity
                        cb.construct(
                                TeamEventOprs.class,
                                eoCurrent.get("oprs"),
                                eoCurrent.get("ddprs"),
                                eoCurrent.get("ccwms")),
                        cb.max(eoMax.get("oprs"))));

        TypedQuery<TeamWithEventStats> query = em.createQuery(cq);
        return query.getResultList();
    }
}
