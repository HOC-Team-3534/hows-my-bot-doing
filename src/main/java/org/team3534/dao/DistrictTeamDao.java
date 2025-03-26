package org.team3534.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import org.team3534.entity.DistrictTeamEntity;

@ApplicationScoped
public class DistrictTeamDao {
    @Inject EntityManager em;

    @Transactional
    public void upsert(DistrictTeamEntity districtTeamEntity) {
        em.merge(districtTeamEntity);
    }

    public void upsert(List<DistrictTeamEntity> districtTeamEntities) {
        districtTeamEntities.forEach(this::upsert);
    }

    public List<DistrictTeamEntity> findAll() {
        var query =
                em.createQuery("SELECT dt FROM DistrictTeamEntity dt", DistrictTeamEntity.class);

        return query.getResultList();
    }
}
