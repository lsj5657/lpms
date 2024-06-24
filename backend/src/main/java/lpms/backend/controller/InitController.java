package lpms.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lpms.backend.entity.SignalData;
import lpms.backend.info.ChInfo;
import lpms.backend.info.FileInfo;
import lpms.backend.info.HeaderInfo;
import lpms.backend.service.SignalDataService;
import lpms.backend.utils.FileUtils;
import lpms.backend.utils.Pair;
import lpms.enums.EventType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static lpms.backend.utils.ProjectConstans.*;

/*@Slf4j
@Controller
@RequiredArgsConstructor
public class InitController {
    private final SignalDataService service;
    private static final String folderPath = Paths.get(USER_DIR,"lpms_binary_data").toString();

    @GetMapping("/init")
    public String init() {

        loadAndSaveBinaryFile();

        return "init";
    }

    private void loadAndSaveBinaryFile()  {
        log.info("file Opened!");
        File folder = new File(folderPath);

        File[] files = folder.listFiles();

        if(files != null) {
            for (File file: files) {
                // 확장자가 .bin인 파일 찾기
                if (file.isFile() && file.getName().endsWith(".bin")) {
                    String filePath = file.getAbsolutePath();
                    String fileName = file.getName();

                    if(service.findByFileName(fileName) != null) {
                        continue;
                    }

                    try {
                        FileInfo fileInfo = FileUtils.fileOpen(filePath);
                        HeaderInfo headerInfo = fileInfo.getHeaderInfo();

                        SignalData signalData = new SignalData();
                        signalData.setFilePath(filePath);
                        signalData.setFileName(fileName);
                        signalData.setTime(headerInfo.getEvent_Date());
                        signalData.setChannel(headerInfo.getEvent_Ch());
                        signalData.setStatus(headerInfo.getAlarm_Result() == 1);
                        EventType eventType = EventType.fromValue(headerInfo.getEventType());
                        signalData.setType(eventType);

                        service.save(signalData);
                    } catch (IOException | ClassNotFoundException e) {
                        log.error("Error processing file: {}", filePath, e);
                    }
                }
            }
        } else {
            log.warn("No files found in directory: {}", folderPath);
        }
    }
}*/

@Slf4j
@Controller
@RequiredArgsConstructor
public class InitController {
    private final SignalDataService service;
    //private static final String folderPath = Paths.get(USER_DIR,"lpms_binary_data").toString();

    private static final String folderPath = Paths.get(USER_DIR,"lpms_binary_data_temp").toString();
    @GetMapping("/init")
    public String init() {

        loadAndSaveBinaryFile();

        return "init";
    }

   /* private void loadAndSaveBinaryFile() {
        log.info("file Opened!");
        File folder = new File(folderPath);

        File[] files = folder.listFiles();

        log.info("files = {}", files);

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    processFilesInDirectory(file);
                }
            }
        } else {
            log.warn("No files found in directory: {}", folderPath);
        }
    }*/

    private void loadAndSaveBinaryFile() {
        log.info("file Opened!");
        processFilesInDirectory(new File(folderPath));


    }

   /* private void processFilesInDirectory(File directory) {
        boolean status = determineStatus(directory);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".bin")) {
                    processFile(file, status);
                }
            }
        }
    }*/
   private void processFilesInDirectory(File directory) {
      // boolean status = determineStatus(directory);
       File[] files = directory.listFiles();

       if (files != null) {
           for (File file : files) {
               if (file.isFile() && file.getName().endsWith(".bin")) {
                   //processFile(file, status);
                   processFile(file);
               }
           }
       }
   }

  /*  private Boolean determineStatus(File directory) {
        String dirName = directory.getName().toLowerCase();
        if (dirName.equals("false")) {
            return true;
        } else if (dirName.equals("impact")) {
            return false;
        }
        return null;
    }*/

    private void processFile(File file
            //, boolean status
    ) {
        String filePath = file.getAbsolutePath();
        String fileName = file.getName();

        if (service.findByFileName(fileName) != null) {
            return;
        }

        try {
            log.info("filePath = {}, fileName = {}", filePath, fileName);
            FileInfo fileInfo = FileUtils.fileOpen(filePath);
            HeaderInfo headerInfo = fileInfo.getHeaderInfo();


            SignalData signalData = new SignalData();
            signalData.setFilePath(filePath);
            signalData.setFileName(fileName);
            signalData.setTime(headerInfo.getEvent_Date());
            signalData.setChannel(headerInfo.getEvent_Ch());
            //signalData.setStatus(status);
            if(headerInfo.getAlarm_Result()==1) signalData.setStatus(true);
            else signalData.setStatus(false);

            EventType eventType = EventType.fromValue(headerInfo.getEventType());
            signalData.setType(eventType);

            service.save(signalData);
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error processing file: {}", filePath, e);
        }
    }
}
