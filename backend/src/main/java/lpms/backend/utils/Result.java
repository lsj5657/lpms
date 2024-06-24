package lpms.backend.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class Result {
    private String imagePath;
    private Boolean aiResult;
    private Float aiProbability;

    public Result(String imagePath, Boolean aiResult, Float aiProbability) {
        this.imagePath = imagePath;
        this.aiResult = aiResult;
        this.aiProbability = aiProbability;
    }
}
