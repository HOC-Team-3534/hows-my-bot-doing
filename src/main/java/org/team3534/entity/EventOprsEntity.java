package org.team3534.entity;

import com.tba.model.EventOPRs;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.util.List;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_oprs")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventOprsEntity {
    @EmbeddedId private EventTeamId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("teamKey")
    @JoinColumn(name = "team_key", insertable = false, updatable = false)
    private TeamEntity team;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventKey")
    @JoinColumn(name = "event_key", insertable = false, updatable = false)
    private EventEntity event;

    private float oprs, ddprs, ccwms;

    public static List<EventOprsEntity> fromEventOprs(
            EventEntity eventEntity,
            List<TeamEntity> teamEntities, 
            EventOPRs eventOprs) {
        var oprs = eventOprs.getOprs();
        var ddprs = eventOprs.getDprs();
        var ccwms = eventOprs.getCcwms();
        return teamEntities.stream()
                .map(
                        teamEntity -> {
                            return new EventOprsEntity(
                                    new EventTeamId(eventEntity.getKey(), teamEntity.getKey()),
                                    teamEntity,
                                    eventEntity,
                                    oprs.get(teamEntity.getKey()),
                                    ddprs.get(teamEntity.getKey()),
                                    ccwms.get(teamEntity.getKey()));
                        })
                .toList();
    }
}
