package org.team3534.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;

import java.util.List;

import org.eclipse.microprofile.metrics.annotation.Timed;
import org.team3534.dao.TeamDao;

@Produces
@ApplicationScoped
@Timed
public class TeamService {

    @Inject
    TeamDao teamDao;

    public List<TeamDao.TeamWithEventStats> getTeamsByEvent(String key) {
        return teamDao.findByEventWithStats(key);
    }
}
