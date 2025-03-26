package org.team3534.entity;

import com.querydsl.core.annotations.QueryEntity;
import com.tba.model.Event;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@QueryEntity
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id")
    private DistrictEntity district;

    @Column(name = "event_year")
    private int year;

    private LocalDate startDate, endDate;

    public static EventEntity fromEvent(Event event) {
        var district = event.getDistrict();
        var districtEntity = district != null ? DistrictEntity.fromDistrict(district) : null;
        return new EventEntity(
                event.getKey(),
                event.getParentEventKey(),
                event.getName(),
                event.getShortName(),
                districtEntity,
                event.getYear(),
                event.getStartDate(),
                event.getEndDate());
    }
}
