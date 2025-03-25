package org.team3534.entity;

import com.tba.model.Team;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    private String name;

    public static TeamEntity fromTeam(Team team) {
        return new TeamEntity(team.getKey(), team.getNickname());
    }
}
