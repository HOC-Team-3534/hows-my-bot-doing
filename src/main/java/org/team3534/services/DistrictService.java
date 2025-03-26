package org.team3534.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Produces;
import java.util.List;
import org.team3534.dao.DistrictDao;
import org.team3534.entity.DistrictEntity;
import org.team3534.sync.DistrictSynchronizer;

@Produces
@ApplicationScoped
public class DistrictService {
    @Inject DistrictDao districtDao;

    @Inject DistrictSynchronizer districtSynchronizer;

    public DistrictEntity getDistrict(String key) {
        var district = districtDao.find(key);

        if (district != null) return district;

        return null;
    }

    @Transactional
    public DistrictEntity getDistrictLoaded(String key) {
        var district = districtDao.find(key);

        if (district != null) {
            district.getEvents();
            district.getDistrictTeams().stream().map(dt -> dt.getTeam()).toList();
            return district;
        }

        return null;
    }

    public List<DistrictEntity> getDistrictsByYear(int year) {
        var districts = districtDao.findByYear(year);

        if (districts.size() > 0) return districts;

        districtSynchronizer.syncDistrictsByYear(year);

        return List.of();
    }
}
