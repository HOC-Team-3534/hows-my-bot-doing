package org.team3534.sync;

import com.tba.api.TeamApi;
import com.tba.api.TeamsApi;
import com.tba.model.Team;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.team3534.dao.TeamDao;
import org.team3534.entity.TeamEntity;

@ApplicationScoped
public class TeamSynchronizer {
    @Inject TeamApi teamApi;
    @Inject TeamsApi teamsApi;

    @Inject TeamDao teamDao;

    public TeamEntity syncTeam(String key) {
        var team = TeamEntity.fromTeam(teamApi.getTeam(key, ""));
        teamDao.upsert(team);
        return team;
    }

    public List<TeamEntity> syncTeamsByYear(int year) {
        List<Team> rawTeams = new ArrayList<>();

        var page = 0;
        while (true) {
            var teams = teamsApi.getTeamsByYear(year, page, "");
            if (teams.size() == 0) break;
            rawTeams.addAll(teams);
            page++;
        }

        var teams = rawTeams.stream().map(TeamEntity::fromTeam).toList();

        teams.forEach(teamDao::upsert);

        return teams;
    }
}
