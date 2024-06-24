package lpms.backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lpms.backend.entity.SignalData;
import lpms.backend.info.HeaderInfo;

@ToString
@Getter @Setter
public class SignalDataDTO {

    private String time;
    private String channel;
    private Boolean status;
    private String type;
    private String fileName;

    public SignalDataDTO(SignalData signalData, HeaderInfo headerInfo) {
        this.time=headerInfo.getEvent_Date();
        this.channel = "V"+ (headerInfo.Event_Ch+101);
        this.status = signalData.getStatus();
        this.type = String.valueOf(signalData.getType());
        this.fileName = signalData.getFileName();
    }

    public SignalDataDTO() {
    }

    public SignalDataDTO(SignalData signalData) {
        this.time=signalData.getTime();
        this.channel = "V"+ (signalData.getChannel()+101);
        this.status = signalData.getStatus();
        this.type = String.valueOf(signalData.getType());
        this.fileName = signalData.getFileName();
    }
}
