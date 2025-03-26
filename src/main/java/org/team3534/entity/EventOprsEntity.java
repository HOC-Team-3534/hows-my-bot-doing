package org.team3534.entity;

import com.querydsl.core.annotations.QueryEntity;
import com.tba.model.EventOPRs;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@QueryEntity
@Entity
@Table(name = "event_oprs")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventOprsEntity {
    @Id private String eventKey;

    private String teamKey;

    private float oprs, ddprs, ccwms;

    public static List<EventOprsEntity> fromEventOprs(String eventKey, EventOPRs eventOprs) {
        var oprs = eventOprs.getOprs();
        var ddprs = eventOprs.getDprs();
        var ccwms = eventOprs.getCcwms();
        return eventOprs.getOprs().keySet().stream()
                .map(
                        teamKey ->
                                new EventOprsEntity(
                                        eventKey,
                                        teamKey,
                                        oprs.get(teamKey),
                                        ddprs.get(teamKey),
                                        ccwms.get(teamKey)))
                .toList();
    }
}
