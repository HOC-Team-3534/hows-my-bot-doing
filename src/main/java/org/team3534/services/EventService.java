package org.team3534.services;

import com.tba.api.EventApi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import java.util.List;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.team3534.dao.EventDao;
import org.team3534.entity.EventEntity;
import org.team3534.sync.EventSynchronizer;

@Produces
@ApplicationScoped
@Timed
public class EventService {

    @Inject EventApi eventApi;

    @Inject EventDao eventDao;

    @Inject EventSynchronizer eventSynchronizer;

    public EventEntity getEvent(String key) {
        var event = eventDao.find(key);

        if (event != null) return event;

        return eventSynchronizer.syncEvent(key);
    }

    public List<EventEntity> getEventsByYear(int year) {
        var events = eventDao.findByYear(year);

        if (events.size() > 0) return events;

        return eventSynchronizer.syncEventsByYear(year);
    }
}
