package lpms.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class BinaryData {
    @Id @GeneratedValue
    @Column(name = "binary_data_id")
    private Long id;

    @OneToOne(mappedBy = "binaryData")
    private SignalData signalData;

    private String binaryPath;
    private String binaryName;
}
