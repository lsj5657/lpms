package lpms.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lpms.backend.dto.SignalDataDTO;
import lpms.backend.entity.SignalData;
import lpms.backend.info.ChInfo;
import lpms.backend.info.FileInfo;
import lpms.backend.info.HeaderInfo;
import lpms.backend.service.SignalProcessingService;
import lpms.backend.utils.*;
import lpms.backend.service.SignalDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


import static lpms.backend.utils.FileUtils.*;
import static lpms.backend.utils.ProjectConstans.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class apiController {
    private final SignalProcessingService signalProcessingService;


    //주어진 파일에 대한 시간 그래프를 요청하는 url
    //선택된 채널이 없으면 이벤트 채널, 있으면 선택된 채널로 수행
    @GetMapping("/api/time/{selectedFileName}")
    public List<DataPoint> timeGraph(@PathVariable String selectedFileName,
                                     @RequestParam(required = false) Integer selectedCh) throws IOException, ClassNotFoundException {

        log.info("Time Graph for fileName={}", selectedFileName);

        return signalProcessingService.processTimeGraph(selectedFileName,selectedCh);
    }


    //주어진 파일에 대한 시간 그래프를 요청하는 url
    //선택된 채널이 없으면 이벤트 채널, 있으면 선택된 채널로 수행
    @GetMapping("/api/freq/{selectedFileName}")
    public List<DataPoint> freqGraph(@PathVariable String selectedFileName,
                                     @RequestParam(required = false) Integer selectedCh) throws IOException, ClassNotFoundException {
        
        log.info("Frequency Graph for fileName={}", selectedFileName);

        return signalProcessingService.processFrequencyGraph(selectedFileName,selectedCh);
    }

    // 주어진 파일에 대한 stft 이미지를 생성하고, ai 결과를 요청하는 url
    // stft는 선택된 채널이 없으면 이벤트 채널, 있으면 선택된 채널로 수행
    // ai는 이벤트 채널로 수행
    @GetMapping("/api/result/{selectedFileName}")
    public Result getResult(@PathVariable String selectedFileName,
                            @RequestParam(required = false) Integer selectedCh) throws IOException, ClassNotFoundException {
        log.info("stft and ai for fileName ={}", selectedFileName);
        log.info("selectedCh = {}", selectedCh);

        return signalProcessingService.processResult(selectedFileName,selectedCh);
    }


    // DB에 저장된 파일
    @GetMapping("/api/files")
    public List<SignalDataDTO> loadFiles() throws IOException, ClassNotFoundException {
        log.info("Loading all files");

        return signalProcessingService.loadAllFiles();
    }





}

