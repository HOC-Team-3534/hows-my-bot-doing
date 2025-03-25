package org.team3534.entity;

import com.tba.model.DistrictList;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    public static DistrictEntity fromDistrict(DistrictList district) {
        return new DistrictEntity(
                district.getKey(),
                district.getDisplayName(),
                district.getAbbreviation(),
                district.getYear());
    }
}
