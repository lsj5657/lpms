package lpms.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Data
public class SignalData {
    @Id @GeneratedValue
    @Column(name = "signal_data_id")
    private Long id;

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "binary_data_id")
    private BinaryData binaryData;

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "graph_data_id")
    private GraphData graphData;
}



