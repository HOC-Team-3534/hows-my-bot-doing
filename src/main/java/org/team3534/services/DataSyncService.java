package org.team3534.services;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.team3534.dao.DistrictDao;
import org.team3534.entity.DistrictEntity;
import org.team3534.entity.EventEntity;
import org.team3534.entity.EventOprsEntity;
import org.team3534.entity.TeamEntity;

import com.tba.api.DistrictApi;
import com.tba.api.DistrictsApi;
import com.tba.api.EventApi;
import com.tba.api.EventsApi;
import com.tba.api.TeamApi;
import com.tba.api.TeamsApi;
import com.tba.model.Event;
import com.tba.model.Team;

@ApplicationScoped
public class DataSyncService {

    @Inject EventApi eventApi;

    @Inject EventsApi eventsApi;

    @Inject TeamApi teamApi;

    @Inject TeamsApi teamsApi;
    
    @Inject DistrictApi districtApi;

    @Inject DistrictsApi districtsApi;

    @Inject DistrictDao districtDao;

    private EventEntity loadEvent(String key) {
        var event = eventApi.getEvent(key, "");
        return EventEntity.fromEvent(event);
    }

    private List<EventEntity> loadEventsByYear(int year) {
        return eventsApi.getEventsByYear(year, "").stream().map(EventEntity::fromEvent).toList();
    }

    private List<EventOprsEntity> loadEventOprs(EventEntity eventEntity, List<TeamEntity> teamEntities) {
        return EventOprsEntity.fromEventOprs(eventEntity, teamEntities, eventApi.getEventOPRs(eventEntity.getKey(), ""));
    }

    public TeamEntity loadTeam(String key) {
        return TeamEntity.fromTeam(teamApi.getTeam(key, ""));
    }

    public List<TeamEntity> loadTeamsByYear(int year){
        var page = new AtomicInteger();

        var teams = new ArrayList<Team>();
        List<Team> pageOfTeams = null;
        do {
            pageOfTeams = teamsApi.getTeamsByYear(year, page.getAndIncrement(), "");
            teams.addAll(pageOfTeams);
        } while (pageOfTeams.size() > 0);

        return teams.stream().map(TeamEntity::fromTeam).toList();
    }

    public List<TeamEntity> loadTeamsByEvent(String eventKey) {
        return eventApi.getEventTeamsSimple(eventKey, "").stream().map(TeamEntity::fromTeam).toList();
    }

    public List<TeamEntity> loadTeamsByDistrict(String districtKey){
        return districtApi.getDistrictTeamsSimple(districtKey, "").stream().map(TeamEntity::fromTeam).toList();
    }

    private List<DistrictEntity> loadDistrictsByYear(int year) {
        return districtsApi.getDistrictsByYear(year, "").stream().map(DistrictEntity::fromDistrict).toList();
    }

    private List<DistrictEntity> loadDistrictsByTeam(String teamKey) {
        return teamApi.getTeamDistricts(teamKey, "").stream().map(DistrictEntity::fromDistrict).toList();
    }

    @Scheduled(every = "P7D")
    public void syncSeasonData() {
        syncSeasonData(Calendar.getInstance().getWeekYear());
    }

    public void syncSeasonData(int year) {
        var eventEntities = loadEventsByYear(year);
        var districtEntities = eventEntities.stream().map(EventEntity::getDistrict).filter(Objects::nonNull).toList();
        var teamEntities = loadTeamsByYear(year);
        var teamsByEventMap = new HashMap<EventEntity, List<TeamEntity>>();
        var eventOprsByEventMap = new HashMap<EventEntity, List<EventOprsEntity>>();
        for(var eventEntity: eventEntities){
            var teamEntitiesFromEvent = loadTeamsByEvent(eventEntity.getKey());
            teamsByEventMap.put(eventEntity, teamEntitiesFromEvent);
            eventOprsByEventMap.put(eventEntity, loadEventOprs(eventEntity, teamEntitiesFromEvent));
        }
        var teamsByDistrictMap = new HashMap<DistrictEntity, List<TeamEntity>>();
        for(var districtEntity: districtEntities){
            teamsByDistrictMap.put(districtEntity, loadTeamsByDistrict(districtEntity.getKey()));
        }
    }

    @Transactional
    public void persistSeasonData(Event event, List<TeamEntity> eventTeams, Map<String, List<DistrictEntity>> teamDistrictsMap, EventOprsEntity eventOPRs) {
        var district = event.getDistrict();
        DistrictEntity districtEntity = districtSynchronizer.syncDistrict(district);

        var eventEntity = eventDao.upsert(EventEntity.fromEvent(districtEntity, event));

        var teamEntities = teamSynchronizer.syncTeams();
        eventTeamDao.upsert(EventTeamEntity.fromEventTeams(eventEntity, teamEntities));
        eventOprsDao.upsert(EventOprsEntity.fromEventOprs(eventEntity, eventOPRs, teamService::getTeam));
        return eventEntity;
    }
}
