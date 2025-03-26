package org.team3534.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import org.team3534.entity.DistrictEntity;

@ApplicationScoped
public class DistrictDao {
    @Inject EntityManager em;

    @Transactional
    public void upsert(DistrictEntity districtEntity) {
        em.merge(districtEntity);
    }

    public DistrictEntity find(String key) {
        return em.find(DistrictEntity.class, key);
    }

    public List<DistrictEntity> findByYear(int year) {
        var query =
                em.createQuery(
                        "SELECT d FROM DistrictEntity d WHERE d.year = :year",
                        DistrictEntity.class);

        query.setParameter("year", year);

        return query.getResultList();
    }
}
