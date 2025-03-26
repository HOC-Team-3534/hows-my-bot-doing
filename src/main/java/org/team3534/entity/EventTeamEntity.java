package org.team3534.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
    @EmbeddedId private EventTeamId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventKey")
    @JoinColumn(name = "event_key")
    private EventEntity event;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("teamKey")
    @JoinColumn(name = "team_key")
    private TeamEntity team;

    public static List<EventTeamEntity> fromEventTeams(
            EventEntity eventEntity, List<TeamEntity> teamEntities) {
        return teamEntities.stream()
                .map(
                        t ->
                                new EventTeamEntity(
                                        new EventTeamId(eventEntity.getKey(), t.getKey()),
                                        eventEntity,
                                        t))
                .toList();
    }
}
