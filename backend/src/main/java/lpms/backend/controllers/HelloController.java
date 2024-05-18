package lpms.backend.controllers;

import lpms.backend.models.DataPoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class HelloController {

    @GetMapping("/api/graph-data")
    public List<DataPoint> getGraphData() {
        // 예시 데이터
        return Arrays.asList(
            new DataPoint(1.0, -1.0),
            new DataPoint(2.0, -4.0),
            new DataPoint(3.0, 9.0),
            new DataPoint(4.0, -6.0),
            new DataPoint(5.0, 25.0)
        );
    }
}
