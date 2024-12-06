package pe.edu.vallegrande.api.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "ip_queries")
public class IpQueriesModel {
    @Id
    private Long id;

    @Column("ip")
    private String ip;

    @Column("city")
    private String city;

    @Column("region")
    private String region;

    @Column("country")
    private String country;

    @Column("loc")
    private String loc;

    @Column("org")
    private String org;

    @Column("postal")
    private String postal;

    @Column("timezone")
    private String timezone;

    @Column("status")
    private String status; // A o I
}
