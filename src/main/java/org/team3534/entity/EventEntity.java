package org.team3534.entity;

import com.tba.model.Event;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventEntity {
    @Id
    @Column(name = "event_key")
    private String key;

    private String parentEventKey;

    private String name;
    private String shortName;

    private String city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_key", nullable = true)
    private DistrictEntity district;

    @Column(name = "event_year")
    private int year;

    private LocalDate startDate, endDate;

    @OneToMany(mappedBy = "event")
    private List<EventTeamEntity> eventTeams;

    @OneToMany(mappedBy = "event")
    private List<EventOprsEntity> oprs;

    public static EventEntity fromEvent(DistrictEntity district, Event event) {
        return new EventEntity(
                event.getKey(),
                event.getParentEventKey(),
                event.getName(),
                event.getShortName(),
                event.getCity(),
                district,
                event.getYear(),
                event.getStartDate(),
                event.getEndDate(),
                new ArrayList<>(),
                new ArrayList<>());
    }
}
