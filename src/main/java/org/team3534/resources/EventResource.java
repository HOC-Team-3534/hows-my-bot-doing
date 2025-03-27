package org.team3534.resources;

import com.tba.api.EventApi;
import com.tba.api.EventsApi;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.team3534.dao.TeamDao.TeamWithEventStats;
import org.team3534.entity.DistrictEntity;
import org.team3534.entity.EventEntity;
import org.team3534.services.EventService;
import org.team3534.services.TeamService;

@Path("/events")
@Produces(MediaType.TEXT_HTML)
public class EventResource {
    @CheckedTemplate
    static class Templates {
        static native TemplateInstance list(
                int year,
                List<Integer> years,
                Collection<DistrictWithEvents> districtEvents,
                Collection<EventWithChildren> events);

        static native TemplateInstance teams(
                EventEntity event, EventEntity parentEvent, List<TeamWithEventStats> teams);
    }

    @Inject EventApi eventApi;

    @Inject EventsApi eventsApi;

    @Inject EventService eventService;

    @Inject TeamService teamService;

    @Value
    @Data
    @RequiredArgsConstructor
    public static class DistrictWithEvents {
        DistrictEntity district;
        List<EventWithChildren> events = new ArrayList<>();
    }

    @Value
    @Data
    @RequiredArgsConstructor
    public static class EventWithChildren {
        EventEntity event;
        List<EventWithChildren> children = new ArrayList<>();
    }

    @GET
    @Path("/{year:\\d+}")
    public TemplateInstance list(int year) {
        var rawEvents = eventService.getEventsByYear(year);

        var districtsMap = new HashMap<String, DistrictWithEvents>();
        var eventsMap = new HashMap<String, EventWithChildren>();

        for (var event : rawEvents) {
            eventsMap.put(event.getKey(), new EventWithChildren(event));
        }

        for (var event : eventsMap.values()) {
            var parent = eventsMap.get(event.event.getParentEventKey());

            if (parent != null) parent.children.add(event);
        }

        var events =
                eventsMap.values().stream()
                        .filter(
                                event ->
                                        event.event.getParentEventKey() == null
                                                || event.event.getParentEventKey() == "")
                        .collect(Collectors.toList());

        for (var event : events) {
            if (event.event.getDistrict() != null) {
                var district = districtsMap.get(event.event.getDistrict().getKey());

                if (district == null) {
                    district = new DistrictWithEvents(event.event.getDistrict());
                    districtsMap.put(district.district.getKey(), district);
                }

                district.events.add(event);
            }
        }

        events =
                events.stream()
                        .filter(event -> event.event.getDistrict() == null)
                        .collect(Collectors.toList());

        return Templates.list(year, List.of(2024, 2025), districtsMap.values(), events);
    }

    @GET
    @Path("/{key}")
    public TemplateInstance teams(String key) {
        var teams = teamService.getTeamsByEvent(key);
        var event = eventService.getEvent(key);

        var parent =
                event.getParentEventKey() == null || event.getParentEventKey() == ""
                        ? null
                        : eventService.getEvent(event.getParentEventKey());

        return Templates.teams(event, parent, teams);
    }
}
