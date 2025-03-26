package org.team3534.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventTeamId implements Serializable {
    @Column(name = "event_key")
    private String eventKey;

    @Column(name = "team_key")
    private String teamKey;
}
