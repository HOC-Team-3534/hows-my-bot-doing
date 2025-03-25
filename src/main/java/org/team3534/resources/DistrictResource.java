package org.team3534.resources;

import com.tba.api.DistrictApi;
import com.tba.api.DistrictsApi;
import com.tba.model.AwardRecipient;
import com.tba.model.DistrictList;
import com.tba.model.DistrictRanking;
import com.tba.model.EventSimple;
import com.tba.model.TeamSimple;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.ArrayList;
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

        static native TemplateInstance rankings(
                DistrictList district, List<DistrictRanking> rankings);

        static native TemplateInstance awards(
                DistrictList district, Map<String, List<AwardRecipient>> awardsMap);
    }

    @Inject DistrictsApi districtsApi;

    @Inject DistrictApi districtApi;

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
        events.sort((event1, event2) -> event1.getEndDate().compareTo(event2.getEndDate()));
        return Templates.events(district, events);
    }

    @GET
    @Path("/{key}/teams")
    public TemplateInstance teams(String key) {
        var teams = districtApi.getDistrictTeamsSimple(key, "");
        var district = getDistrict(key);
        return Templates.teams(district, teams);
    }

    @GET
    @Path("/{key}/rankings")
    public TemplateInstance rankings(String key) {
        var rankings = districtApi.getDistrictRankings(key, "");
        var district = getDistrict(key);
        return Templates.rankings(district, rankings);
    }

    @GET
    @Path("/{key}/awards")
    public TemplateInstance awards(String key) {
        var awards = districtApi.getDistrictAwards(key, "");
        var district = getDistrict(key);

        var awardsMap = new HashMap<String, List<AwardRecipient>>();

        for (var award : awards) {
            var m = awardsMap.get(award.getName());

            if (m == null) {
                m = new ArrayList<AwardRecipient>();
                awardsMap.put(award.getName(), m);
            }

            m.addAll(award.getRecipientList());
        }

        return Templates.awards(district, awardsMap);
    }

    Map<String, DistrictList> districtMap = new HashMap<>();

    DistrictList getDistrict(String key) {
        var district = districtMap.get(key);

        if (district != null) return district;

        var events = districtApi.getDistrictEventsSimple(key, "");
        district = events.get(0).getDistrict();

        addDistrict(key, district);

        return district;
    }

    void addDistrict(String key, DistrictList district) {
        districtMap.put(key, district);
    }
}
