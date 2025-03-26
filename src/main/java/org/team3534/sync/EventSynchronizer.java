package org.team3534.sync;

import com.tba.api.EventApi;
import com.tba.api.EventsApi;
import com.tba.model.Event;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.team3534.dao.EventDao;
import org.team3534.dao.EventOprsDao;
import org.team3534.dao.EventTeamDao;
import org.team3534.entity.DistrictEntity;
import org.team3534.entity.EventEntity;
import org.team3534.entity.EventOprsEntity;
import org.team3534.entity.EventTeamEntity;
import org.team3534.services.TeamService;

@ApplicationScoped
@Timed
public class EventSynchronizer {
    @Inject EventApi eventApi;
    @Inject EventsApi eventsApi;

    @Inject EventDao eventDao;

    @Inject EventTeamDao eventTeamDao;

    @Inject EventOprsDao eventOprsDao;

    @Inject DistrictSynchronizer districtSynchronizer;

    @Inject TeamSynchronizer teamSynchronizer;

    @Inject TeamService teamService;

    public void syncEvent(String key) {
        eventApi.getEvent(key, "").subscribe().with(this::syncEvent);
    }

    public EventEntity syncEvent(Event event) {
        var district = event.getDistrict();
        DistrictEntity districtEntity = null;
        if (district != null) districtEntity = districtSynchronizer.syncDistrict(district);

        var eventEntity = EventEntity.fromEvent(districtEntity, event);
        eventDao.upsert(eventEntity);
        syncEventTeams(eventEntity);
        syncEventOprs(eventEntity);
        return eventEntity;
    }

    public void syncEventsByYear(int year) {
        eventsApi
                .getEventsByYear(year, "")
                .subscribe()
                .with(events -> events.forEach(this::syncEvent));
    }

    public void syncEventTeams(EventEntity eventEntity) {
        eventApi.getEventTeamsSimple(eventEntity.getKey(), "")
                .map(teams -> teams.stream().map(teamSynchronizer::syncTeam).toList())
                .map(teamEntities -> EventTeamEntity.fromEventTeams(eventEntity, teamEntities))
                .subscribe()
                .with(eventTeamDao::upsert);
    }

    public void syncEventOprs(EventEntity eventEntity) {
        eventApi.getEventOPRs(eventEntity.getKey(), "")
                .map(
                        eventOprs ->
                                EventOprsEntity.fromEventOprs(
                                        eventEntity, eventOprs, teamService::getTeam))
                .subscribe()
                .with(eventOprsDao::upsert);
    }
}
