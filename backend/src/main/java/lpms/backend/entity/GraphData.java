package lpms.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Data
public class GraphData {

    @Id @GeneratedValue
    @Column(name = "graph_data_id")
    private Long id;

    @OneToOne(mappedBy = "graphData",fetch= LAZY)
    private SignalData signalData;

    private String graphFilePath;
    private String graphFileName;
}
