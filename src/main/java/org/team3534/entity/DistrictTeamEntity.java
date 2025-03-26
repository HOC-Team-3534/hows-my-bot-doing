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
@Table(name = "district_team")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistrictTeamEntity {
    @EmbeddedId private DistrictTeamId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("districtKey")
    @JoinColumn(name = "district_key")
    private DistrictEntity district;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("teamKey")
    @JoinColumn(name = "team_key")
    private TeamEntity team;

    public static List<DistrictTeamEntity> fromDistrictTeams(
            DistrictEntity districtEntity, List<TeamEntity> teamEntities) {
        return teamEntities.stream()
                .map(
                        t ->
                                new DistrictTeamEntity(
                                        new DistrictTeamId(districtEntity.getKey(), t.getKey()),
                                        districtEntity,
                                        t))
                .toList();
    }

    public static List<DistrictTeamEntity> fromTeamDistricts(
            TeamEntity teamEntity, List<DistrictEntity> districtEntities) {
        return districtEntities.stream()
                .map(
                        d ->
                                new DistrictTeamEntity(
                                        new DistrictTeamId(d.getKey(), teamEntity.getKey()),
                                        d,
                                        teamEntity))
                .toList();
    }
}
