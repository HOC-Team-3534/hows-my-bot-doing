package org.team3534.resources;

import com.tba.api.EventApi;
import com.tba.api.EventsApi;
import com.tba.model.DistrictList;
import com.tba.model.Event;
import com.tba.model.TeamSimple;
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
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

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

        static native TemplateInstance teams(Event event, Event parentEvent, List<TeamSimple> teams);
    }

    @Inject EventApi eventApi;

    @Inject EventsApi eventsApi;

    @Value
    @Data
    @RequiredArgsConstructor
    public static class DistrictWithEvents {
        DistrictList district;
        List<EventWithChildren> events = new ArrayList<>();
    }

    @Value
    @Data
    @RequiredArgsConstructor
    public static class EventWithChildren {
        Event event;
        List<EventWithChildren> children = new ArrayList<>();
    }

    @GET
    @Path("/{year:\\d+}")
    public TemplateInstance list(int year) {
        var rawEvents = eventsApi.getEventsByYear(year, "");

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
        var teams = eventApi.getEventTeamsSimple(key, "");
        var event = getEvent(key);

        var parent = event.getParentEventKey() == null || event.getParentEventKey() == "" ? null : getEvent(event.getParentEventKey());
        return Templates.teams(event, parent, teams);
    }

    Map<String, Event> eventMap = new HashMap<>();

    Event getEvent(String key) {
        var event = eventMap.get(key);

        if (event != null) return event;

        event = eventApi.getEvent(key, "");

        addEvent(key, event);

        return event;
    }

    void addEvent(String key, Event event) {
        eventMap.put(key, event);
    }
}
