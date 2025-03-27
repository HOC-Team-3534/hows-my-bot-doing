package org.team3534.sync;

import com.tba.api.TeamApi;
import com.tba.api.TeamsApi;
import com.tba.model.Team;
import com.tba.model.TeamSimple;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.team3534.dao.DistrictTeamDao;
import org.team3534.dao.TeamDao;
import org.team3534.entity.DistrictTeamEntity;
import org.team3534.entity.TeamEntity;

@ApplicationScoped
public class TeamSynchronizer {
    @Inject TeamApi teamApi;
    @Inject TeamsApi teamsApi;

    @Inject TeamDao teamDao;

    @Inject DistrictSynchronizer districtSynchronizer;

    @Inject DistrictTeamDao districtTeamDao;

    static TeamSimple toTeamSimple(Team team) {
        return new TeamSimple(
                team.getKey(),
                team.getTeamNumber(),
                team.getNickname(),
                team.getName(),
                team.getCity(),
                team.getStateProv(),
                team.getCountry());
    }

    public void syncTeam(String key) {
        syncTeam(teamApi.getTeam(key, ""));
    }

    public TeamEntity syncTeam(TeamSimple teamSimple) {
        var teamEntity = TeamEntity.fromTeam(teamSimple);
        teamDao.upsert(teamEntity);
        syncTeamDistricts(teamEntity);
        return teamEntity;
    }

    public TeamEntity syncTeam(Team team) {
        return syncTeam(toTeamSimple(team));
    }

    public void syncTeamDistricts(TeamEntity teamEntity) {
        var teamDistricts = teamApi.getTeamDistricts(teamEntity.getKey(), "");
        var districtEntities =
                teamDistricts.stream().map(districtSynchronizer::syncDistrict).toList();
        var districtTeamEntities =
                DistrictTeamEntity.fromTeamDistricts(teamEntity, districtEntities);
        districtTeamEntities.forEach(districtTeamDao::upsert);
    }

    public void syncTeamsByYear(int year) {
        var page = new AtomicInteger();

        var teams = new ArrayList<Team>();
        List<Team> pageOfTeams = null;
        do {
            pageOfTeams = teamsApi.getTeamsByYear(year, page.getAndIncrement(), "");
            teams.addAll(pageOfTeams);
        } while (pageOfTeams.size() > 0);

        teams.forEach(this::syncTeam);
    }
}
