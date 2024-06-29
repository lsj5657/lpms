package lpms.backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lpms.backend.entity.SignalData;
import lpms.backend.info.HeaderInfo;

/**
 * Data Transfer Object (DTO) for SignalData entity.
 * This class is used to transfer data between different layers of the application.
 */
@ToString
@Getter @Setter
public class SignalDataDTO {

    // Timestamp of the signal data
    private String time;

    // Channel information, formatted as "V" followed by the channel number plus 101
   // private String channel;

    // Status of the signal data
    private Boolean status;

    // Type of the signal data as a String
    //private String type;

    // Filename associated with the signal data
    private String fileName;

    /**
     * Constructor that initializes the DTO using a SignalData entity.
     *
     * @param signalData The SignalData entity to be converted into a DTO
     */
    public SignalDataDTO(SignalData signalData) {
        this.time = signalData.getTime();
        //this.channel = "V" + (signalData.getChannel() + 101);
        this.status = signalData.getStatus();
        //this.type = String.valueOf(signalData.getType());
        this.fileName = signalData.getFileName();
    }
}