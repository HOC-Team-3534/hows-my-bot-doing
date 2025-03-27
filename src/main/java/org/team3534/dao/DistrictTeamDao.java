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
    public DistrictTeamEntity upsert(DistrictTeamEntity districtTeamEntity) {
        return em.merge(districtTeamEntity);
    }

    @Transactional
    public List<DistrictTeamEntity> upsert(List<DistrictTeamEntity> districtTeamEntities) {
        return districtTeamEntities.stream().map(em::merge).toList();
    }

    public List<DistrictTeamEntity> findAll() {
        var query =
                em.createQuery("SELECT dt FROM DistrictTeamEntity dt", DistrictTeamEntity.class);

        return query.getResultList();
    }
}
