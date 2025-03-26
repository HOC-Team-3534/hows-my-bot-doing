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
public class DistrictTeamId implements Serializable {
    @Column(name = "district_key")
    private String districtKey;

    @Column(name = "team_key")
    private String teamKey;
}
