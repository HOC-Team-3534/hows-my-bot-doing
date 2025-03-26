package org.team3534.sync;

import com.tba.api.TeamApi;
import com.tba.api.TeamsApi;
import com.tba.model.Team;
import com.tba.model.TeamSimple;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.team3534.dao.DistrictTeamDao;
import org.team3534.dao.TeamDao;
import org.team3534.entity.DistrictTeamEntity;
import org.team3534.entity.TeamEntity;

@ApplicationScoped
@Timed
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
        teamApi.getTeam(key, "").subscribe().with(this::syncTeam);
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
        teamApi.getTeamDistricts(teamEntity.getKey(), "")
                .map(
                        teamDistricts ->
                                teamDistricts.stream()
                                        .map(districtSynchronizer::syncDistrict)
                                        .toList())
                .map(
                        districtEntities ->
                                DistrictTeamEntity.fromTeamDistricts(teamEntity, districtEntities))
                .subscribe()
                .with(districtTeamDao::upsert);
    }

    public void syncTeamsByYear(int year) {
        Multi.createBy()
                .repeating()
                .uni(
                        () -> new AtomicInteger(),
                        // For each page index, call teamsApi.getTeamsByYear(...)
                        state -> teamsApi.getTeamsByYear(year, state.getAndIncrement(), ""))
                // Stop once we get an empty list
                .until(teams -> !teams.isEmpty())
                .toUni()
                .subscribe()
                .with(teams -> teams.forEach(this::syncTeam));
    }
}
