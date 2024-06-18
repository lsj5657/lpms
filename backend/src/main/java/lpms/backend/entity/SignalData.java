package lpms.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lpms.enums.EventType;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Data
public class SignalData {
    @Id @GeneratedValue
    @Column(name = "signal_data_id")
    private Long id;


    private String filePath;
    private String fileName;
    private Boolean status;

    @Enumerated(EnumType.STRING)
    private EventType Type;

    private Boolean aiResult;
}



