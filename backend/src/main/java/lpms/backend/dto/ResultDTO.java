package lpms.backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) for the results of a signal processing operation.
 */
@Getter @Setter
@ToString
public class ResultDTO {
    private String imagePath;
    private Boolean aiResult;
    private Float aiProbability;

    public ResultDTO(String imagePath, Boolean aiResult, Float aiProbability) {
        this.imagePath = imagePath;
        this.aiResult = aiResult;
        this.aiProbability = aiProbability;
    }
}
