package org.team3534.sync;

import java.util.List;

import org.team3534.dao.DistrictDao;
import org.team3534.entity.DistrictEntity;

import com.tba.api.DistrictApi;
import com.tba.api.DistrictsApi;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DistrictSynchronizer {
    @Inject
    DistrictApi districtApi;
    @Inject
    DistrictsApi districtsApi;

    @Inject
    DistrictDao districtDao;

    public List<DistrictEntity> syncDistrictsByYear(int year) {
        var districts = districtsApi.getDistrictsByYear(year, "").stream()
                .map(DistrictEntity::fromDistrict)
                .toList();

        districts.forEach(districtDao::upsert);

        return districts;
    }
}
