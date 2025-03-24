package org.team3534.resources;

import com.tba.api.DistrictApi;
import com.tba.api.DistrictsApi;
import com.tba.model.DistrictList;
import com.tba.model.EventSimple;
import com.tba.model.TeamSimple;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/districts")
@Produces(MediaType.TEXT_HTML)
public class DistrictResource {
    @CheckedTemplate
    static class Templates {
        static native TemplateInstance list(
                int year, List<Integer> years, List<DistrictList> districts);

        static native TemplateInstance events(DistrictList district, List<EventSimple> events);

        static native TemplateInstance teams(DistrictList district, List<TeamSimple> teams);
    }

    @Inject
    DistrictsApi districtsApi;

    @Inject
    DistrictApi districtApi;

    @GET
    @Path("/{year:\\d+}")
    public TemplateInstance list(int year) {
        var districts = districtsApi.getDistrictsByYear(year, "");
        return Templates.list(year, List.of(2024, 2025), districts);
    }

    @GET
    @Path("/{key}/events")
    public TemplateInstance events(String key) {
        var events = districtApi.getDistrictEventsSimple(key, "");
        var district = events.get(0).getDistrict();
        addDistrict(key, district);
        return Templates.events(district, events);
    }

    @GET
    @Path("/{key}/teams")
    public TemplateInstance teams(String key) {
        var teams = districtApi.getDistrictTeamsSimple(key, "");
        var district = getDistrict(key);
        return Templates.teams(district, teams);
    }

    Map<String, DistrictList> districtMap = new HashMap<>();

    DistrictList getDistrict(String key) {
        var district = districtMap.get(key);

        if (district != null)
            return district;

        var events = districtApi.getDistrictEventsSimple(key, "");
        district = events.get(0).getDistrict();

        addDistrict(key, district);

        return district;
    }

    void addDistrict(String key, DistrictList district) {
        districtMap.put(key, district);
    }
}
