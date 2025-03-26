package org.team3534.entity;

import com.tba.model.Team;
import com.tba.model.TeamSimple;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "team")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamEntity {
    @Id
    @Column(name = "team_key")
    private String key;

    private int teamNumber;

    private String name;

    private String city;

    @OneToMany(mappedBy = "team")
    private List<EventTeamEntity> eventTeams;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private List<EventOprsEntity> eventOprs = new ArrayList<>();

    @OneToMany(mappedBy = "team")
    private List<DistrictTeamEntity> teamDistricts;

    public static TeamEntity fromTeam(Team team) {
        return new TeamEntity(
                team.getKey(),
                team.getTeamNumber(),
                team.getNickname(),
                team.getCity(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());
    }

    public static TeamEntity fromTeam(TeamSimple teamSimple) {
        return new TeamEntity(
                teamSimple.getKey(),
                teamSimple.getTeamNumber(),
                teamSimple.getNickname(),
                teamSimple.getCity(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());
    }
}
