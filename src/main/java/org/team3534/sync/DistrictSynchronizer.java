package org.team3534.sync;

import com.tba.api.DistrictApi;
import com.tba.api.DistrictsApi;
import com.tba.model.DistrictList;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.team3534.dao.DistrictDao;
import org.team3534.entity.DistrictEntity;

@ApplicationScoped
public class DistrictSynchronizer {
    @Inject DistrictApi districtApi;
    @Inject DistrictsApi districtsApi;

    @Inject DistrictDao districtDao;

    public void syncDistrictsByYear(int year) {
        districtsApi.getDistrictsByYear(year, "").stream()
                .map(DistrictEntity::fromDistrict)
                .forEach(districtDao::upsert);
    }

    public DistrictEntity syncDistrict(DistrictList district) {
        var districtEntity = DistrictEntity.fromDistrict(district);
        districtDao.upsert(districtEntity);
        return districtEntity;
    }
}
