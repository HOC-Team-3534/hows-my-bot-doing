package org.team3534.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import java.util.List;
import org.team3534.dao.TeamDao;
import org.team3534.entity.TeamEntity;
import org.team3534.sync.TeamSynchronizer;

@Produces
@ApplicationScoped
public class TeamService {

    @Inject TeamDao teamDao;

    @Inject TeamSynchronizer teamSynchronizer;

    public List<TeamDao.TeamWithEventStats> getTeamsByEvent(String key) {
        return teamDao.findByEventWithStats(key);
    }

    public TeamEntity getTeam(String key) {
        var teamEntity = teamDao.find(key);

        if (teamEntity != null) return teamEntity;

        teamSynchronizer.syncTeam(key);

        return null;
    }
}
