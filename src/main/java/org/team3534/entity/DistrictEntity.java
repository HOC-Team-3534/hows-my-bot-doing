package org.team3534.entity;

import com.tba.model.DistrictList;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "district")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistrictEntity {
    @Id
    @Column(name = "district_key")
    private String key;

    private String name;
    private String abbr;

    @Column(name = "district_year")
    private int year;

    @OneToMany(mappedBy = "district", fetch = FetchType.LAZY)
    private List<EventEntity> events = new ArrayList<>();

    @OneToMany(mappedBy = "district")
    private List<DistrictTeamEntity> districtTeams;

    public static DistrictEntity fromDistrict(DistrictList district) {
        return new DistrictEntity(
                district.getKey(),
                district.getDisplayName(),
                district.getAbbreviation(),
                district.getYear(),
                new ArrayList<>(),
                new ArrayList<>());
    }
}
