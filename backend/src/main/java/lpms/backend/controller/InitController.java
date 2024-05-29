package lpms.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lpms.backend.entity.BinaryData;
import lpms.backend.entity.SignalData;
import lpms.backend.service.SignalDataService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;

@Slf4j
@Controller
@RequiredArgsConstructor
public class InitController {
    private final SignalDataService service;
    private static final String folderPath = "D:\\lpms_binary_data";

    @GetMapping("/init")
    public String init(){
        loadBinaryFile();
        return "init";
    }

    private void loadBinaryFile() {
        log.info("file Opened!");
        File folder = new File(folderPath);

        File[] files = folder.listFiles();

        if(files!=null){
            for (File file: files){
                // 확장자가 .bin인 파일 찾기
                if (file.isFile() && file.getName().endsWith(".bin")) {
                    String filePath = file.getAbsolutePath();
                    String fileName = file.getName();

                    log.info("filePath={}", filePath);
                    log.info("fileName={}", fileName);
                    BinaryData binaryData = new BinaryData();
                    binaryData.setBinaryPath(filePath);
                    binaryData.setBinaryName(fileName);
                    SignalData signalData = new SignalData();
                    signalData.setBinaryData(binaryData);
                    service.save(signalData);

                }
            }
        }

    }
}
