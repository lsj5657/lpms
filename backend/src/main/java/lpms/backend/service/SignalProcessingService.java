package lpms.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lpms.backend.dto.SignalDataDTO;
import lpms.backend.entity.SignalData;

import lpms.backend.info.FileInfo;

import lpms.backend.utils.DataPoint;

import lpms.backend.dto.ResultDTO;
import lpms.backend.utils.FileUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static lpms.backend.utils.FileUtils.fileOpen;
import static lpms.backend.utils.FileUtils.removeBinExtension;


@Service
@RequiredArgsConstructor
@Slf4j
public class SignalProcessingService {
    private final SignalDataService service;
    private final DataProcessor dataProcessor;
    private final PythonScriptExecutor pythonScriptExecutor;
    /**
     * Processes the time graph for the specified file and channel.
     *
     * @param selectedFileName the name of the file
     * @param selectedCh the selected channel, can be null
     * @return a list of DataPoints representing the time graph
     */
    public List<DataPoint> processTimeGraph(String selectedFileName, Integer selectedCh) {
        try {
            SignalData signalData = service.findByFileName(selectedFileName);
            FileInfo fileInfo = fileOpen(signalData.getFilePath());
            int channelId = dataProcessor.getChannelId(selectedCh, fileInfo);
            List<Float> floatList = dataProcessor.getListByCh(fileInfo, channelId);
            return dataProcessor.getTimeDataPoint(floatList);
        } catch (Exception e) {
            log.error("Error processing time graph for file: {}", selectedFileName, e);
            return new ArrayList<>();
        }
    }


    /**
     * Processes the frequency graph for the specified file and channel.
     *
     * @param selectedFileName the name of the file
     * @param selectedCh the selected channel, can be null
     * @return a list of DataPoints representing the frequency graph
     */
    public List<DataPoint> processFrequencyGraph(String selectedFileName, Integer selectedCh)  {
        try {
            SignalData signalData = service.findByFileName(selectedFileName);
            FileInfo fileInfo = fileOpen(signalData.getFilePath());
            int channelId = dataProcessor.getChannelId(selectedCh, fileInfo);
            List<Float> floatList = dataProcessor.getListByCh(fileInfo, channelId);
            pythonScriptExecutor.performFFT(floatList, signalData.getFileName());
            return dataProcessor.getFrequencyDataPoint(selectedFileName);
        } catch (Exception e) {
            log.error("Error processing frequency graph for file: {}", selectedFileName, e);
            return new ArrayList<>();
        }
    }


    /**
     * Processes the STFT image and AI results for the specified file and channel.
     *
     * @param selectedFileName the name of the file
     * @param selectedCh the selected channel, can be null
     * @return a Result object containing the STFT image path, AI result, and AI probability
     */
    public ResultDTO processResult(String selectedFileName, Integer selectedCh) {
        try {
            SignalData signalData = service.findByFileName(selectedFileName);
            FileInfo fileInfo = fileOpen(signalData.getFilePath());
            int channelId = dataProcessor.getChannelId(selectedCh, fileInfo);
            List<Float> listByCh = dataProcessor.getListByCh(fileInfo, channelId);
            List<Float> listByEventCh = dataProcessor.getListByCh(fileInfo, fileInfo.getHeaderInfo().getEventCh());
            log.info("stft and ai start");
            pythonScriptExecutor.performSTFTAndAI(listByCh, listByEventCh, signalData);
            log.info("stft and ai end");

            String imagePath = "/image/" + removeBinExtension(selectedFileName) + ".png";
            Boolean aiResult = signalData.getAiResult();
            Float aiProbability = signalData.getAiProbability();
            log.info("Result = {} ", new ResultDTO(imagePath, aiResult, aiProbability));
            return new ResultDTO(imagePath, aiResult, aiProbability);
        } catch (Exception e) {
            log.error("Error processing result for file: {}", selectedFileName, e);
            return new ResultDTO("/image/error.png", false, 0.0f);
        }
    }

    public ResultDTO processResultV2(String selectedFileName, Integer selectedCh) {
        try {
            SignalData signalData = service.findByFileName(selectedFileName);
            FileInfo fileInfo = fileOpen(signalData.getFilePath());
            int channelId = dataProcessor.getChannelId(selectedCh, fileInfo);
            List<Float> listByCh = dataProcessor.getListByCh(fileInfo, channelId);
            //List<Float> listByEventCh = dataProcessor.getListByCh(fileInfo, fileInfo.getHeaderInfo().getEventCh());
            log.info("stft and ai start");
            pythonScriptExecutor.performSTFTAndAIV2(listByCh, FileUtils.removeBinExtension(selectedFileName), signalData);
            log.info("stft and ai end");

            String imagePath = "/image/" + removeBinExtension(selectedFileName) + ".png";
            Boolean aiResult = signalData.getAiResult();
            Float aiProbability = signalData.getAiProbability();
            log.info("Result = {} ", new ResultDTO(imagePath, aiResult, aiProbability));
            return new ResultDTO(imagePath, aiResult, aiProbability);
        } catch (Exception e) {
            log.error("Error processing result for file: {}", selectedFileName, e);
            return new ResultDTO("/image/error.png", false, 0.0f);
        }
    }


    /**
     * Loads all files stored in the database.
     *
     * @return a list of SignalDataDTO objects representing all stored files
     */
    public List<SignalDataDTO> loadAllFiles() {

        List<SignalData> signalDataList = service.findAll();
        List<SignalDataDTO> signalDataDTOList = new ArrayList<>();
        for (SignalData signalData : signalDataList) {
            signalDataDTOList.add(new SignalDataDTO(signalData));
        }
        return signalDataDTOList;
    }

}