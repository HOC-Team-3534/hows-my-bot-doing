package org.team3534.resources;

import com.tba.api.DistrictApi;
import com.tba.api.DistrictsApi;
import com.tba.model.AwardRecipient;
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
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.team3534.entity.DistrictEntity;
import org.team3534.services.DistrictService;

@Path("/districts")
@Produces(MediaType.TEXT_HTML)
@Timed
public class DistrictResource {
    @CheckedTemplate
    static class Templates {
        static native TemplateInstance list(
                int year, List<Integer> years, List<DistrictEntity> districts);

        static native TemplateInstance events(DistrictEntity district, List<EventSimple> events);

        static native TemplateInstance teams(DistrictEntity district, List<TeamSimple> teams);

        static native TemplateInstance rankings(
                DistrictEntity district, List<DistrictRanking> rankings);

        static native TemplateInstance awards(
                DistrictEntity district, Map<String, List<AwardRecipient>> awardsMap);
    }

    @Inject DistrictsApi districtsApi;

    @Inject DistrictApi districtApi;

    @Inject DistrictService districtService;

    @GET
    @Path("/")
    public Response list() {
        int year = Calendar.getInstance().getWeekYear();
        return Response.seeOther(URI.create("/districts/" + year)).build();
    }

    @GET
    @Path("/{year:\\d+}")
    public TemplateInstance list(int year) {
        var districts = districtService.getDistrictsByYear(year);
        return Templates.list(year, List.of(2024, 2025), districts);
    }

    @GET
    @Path("/{key}")
    public Response dsitrict(String key) {
        return Response.seeOther(URI.create("/districts/" + key + "/events")).build();
    }

    @GET
    @Path("/{key}/events")
    public TemplateInstance events(String key) {
        var events = districtApi.getDistrictEventsSimple(key, "");
        var district = districtService.getDistrict(key);
        events.sort((event1, event2) -> event1.getEndDate().compareTo(event2.getEndDate()));
        return Templates.events(district, events);
    }

    @GET
    @Path("/{key}/teams")
    public TemplateInstance teams(String key) {
        var teams = districtApi.getDistrictTeamsSimple(key, "");
        var district = districtService.getDistrict(key);
        return Templates.teams(district, teams);
    }

    @GET
    @Path("/{key}/rankings")
    public TemplateInstance rankings(String key) {
        var rankings = districtApi.getDistrictRankings(key, "");
        var district = districtService.getDistrict(key);
        return Templates.rankings(district, rankings);
    }

    @GET
    @Path("/{key}/awards")
    public TemplateInstance awards(String key) {
        var awards = districtApi.getDistrictAwards(key, "");
        var district = districtService.getDistrict(key);

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
}
