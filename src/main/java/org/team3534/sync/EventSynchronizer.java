package org.team3534.sync;

import com.tba.api.EventApi;
import com.tba.api.EventsApi;
import com.tba.model.TeamSimple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import org.team3534.dao.EventDao;
import org.team3534.dao.EventOprsDao;
import org.team3534.dao.EventTeamDao;
import org.team3534.entity.EventEntity;
import org.team3534.entity.EventOprsEntity;
import org.team3534.entity.EventTeamEntity;

@ApplicationScoped
public class EventSynchronizer {
    @Inject
    EventApi eventApi;
    @Inject
    EventsApi eventsApi;

    @Inject
    EventDao eventDao;

    @Inject
    EventTeamDao eventTeamDao;

    @Inject
    EventOprsDao eventOprsDao;

    public EventEntity syncEvent(String key) {
        var event = EventEntity.fromEvent(eventApi.getEvent(key, ""));
        eventDao.upsert(event);
        return event;
    }

    public List<EventEntity> syncEventsByYear(int year) {
        var events = eventsApi.getEventsByYear(year, "").stream().map(EventEntity::fromEvent).toList();

        events.forEach(eventDao::upsert);

        return events;
    }

    public void syncEventTeams(String key) {
        var eventTeams = eventApi.getEventTeamsSimple(key, "");

        EventTeamEntity.fromEventTeams(key, eventTeams.stream().map(TeamSimple::getKey).toList())
                .forEach(eventTeamDao::upsert);
    }

    public void syncEventOprs(String key) {
        var eventOprs = eventApi.getEventOPRs(key, "");

        EventOprsEntity.fromEventOprs(key, eventOprs).forEach(eventOprsDao::upsert);
    }
}
