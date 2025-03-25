package org.team3534.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_team")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventTeamEntity {
    @Id private String eventKey;
    @Id private String teamKey;

    public static List<EventTeamEntity> fromEventTeams(String eventKey, List<String> teamKeys) {
        return teamKeys.stream().map(t -> new EventTeamEntity(eventKey, t)).toList();
    }
}
