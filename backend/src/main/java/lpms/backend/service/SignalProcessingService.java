package lpms.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lpms.backend.dto.SignalDataDTO;
import lpms.backend.entity.SignalData;
import lpms.backend.info.ChInfo;
import lpms.backend.info.FileInfo;
import lpms.backend.utils.ByteUtils;
import lpms.backend.utils.DataPoint;
import lpms.backend.utils.Pair;
import lpms.backend.utils.Result;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static lpms.backend.utils.FileUtils.fileOpen;
import static lpms.backend.utils.FileUtils.removeBinExtension;
import static lpms.backend.utils.ProjectConstans.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignalProcessingService {
    private final SignalDataService service;

    public List<DataPoint> processTimeGraph(String selectedFileName, Integer selectedCh) {
        try {
            SignalData signalData = service.findByFileName(selectedFileName);
            FileInfo fileInfo = fileOpen(signalData.getFilePath());
            int channelId = getChannelId(selectedCh, fileInfo);
            List<Float> floatList = getListByCh(fileInfo, channelId);
            return getTimeDataPoint(floatList);
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error processing time graph for file: {}", selectedFileName, e);
            return new ArrayList<>();
        }
    }

    public List<DataPoint> processFrequencyGraph(String selectedFileName, Integer selectedCh)  {
        try {
            SignalData signalData = service.findByFileName(selectedFileName);
            FileInfo fileInfo = fileOpen(signalData.getFilePath());
            int channelId = getChannelId(selectedCh, fileInfo);
            List<Float> floatList = getListByCh(fileInfo, channelId);
            performFFT(floatList, signalData.getFileName());
            return getFrequencyDataPoint(selectedFileName);
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error processing frequency graph for file: {}", selectedFileName, e);
            return new ArrayList<>();
        }
    }

    public Result processResult(String selectedFileName, Integer selectedCh) {
        try {
            SignalData signalData = service.findByFileName(selectedFileName);
            FileInfo fileInfo = fileOpen(signalData.getFilePath());
            int channelId = getChannelId(selectedCh, fileInfo);
            List<Float> listByCh = getListByCh(fileInfo, channelId);
            List<Float> listByEventCh = getListByCh(fileInfo, fileInfo.getHeaderInfo().getEvent_Ch());
            performSTFTAndAI(listByCh, listByEventCh, signalData);

            String imagePath = "/image/" + removeBinExtension(selectedFileName) + ".png";
            Boolean aiResult = signalData.getAiResult();
            Float aiProbability = signalData.getAiProbability();

            return new Result(imagePath, aiResult, aiProbability);
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error processing result for file: {}", selectedFileName, e);
            return new Result("/image/error.png", false, 0.0f);
        }
    }

    public List<SignalDataDTO> loadAllFiles() {

        List<SignalData> signalDataList = service.findAll();
        List<SignalDataDTO> signalDataDTOList = new ArrayList<>();
        for (SignalData signalData : signalDataList) {
            signalDataDTOList.add(new SignalDataDTO(signalData));
        }
        return signalDataDTOList;
    }

    private void performFFT(List<Float> data, String fileName) {
        String pythonScriptPath = Paths.get(PYTHON_SCRIPT_PATH, "fft.py").toString();
        executePythonScript(pythonScriptPath, data, removeBinExtension(fileName));
    }

    private void performSTFTAndAI(List<Float> data,List<Float> eventData, SignalData signalData) {
        String stftScriptPath = Paths.get(PYTHON_SCRIPT_PATH, "stft.py").toString();
        String aiScriptPath = Paths.get(PYTHON_SCRIPT_PATH, "ai.py").toString();
        String weightPath = Paths.get(USER_DIR, "42_3.587652378297776e-05.pth").toString();
        executePythonScript(stftScriptPath, data, removeBinExtension(signalData.getFileName()));
        if(signalData.getAiProbability()==null)executePythonScriptWithWeight(aiScriptPath, eventData, signalData, weightPath);
    }

    private void executePythonScript(String scriptPath, List<Float> data, String fileName) {
        try {
            log.info("Executing Python script: {}", scriptPath);
            ProcessBuilder pb = new ProcessBuilder(PYTHON_EXECUTABLE_PATH, scriptPath);
            Process process = pb.start();
            sendPythonData(process, data, fileName);
            logPythonOutput(process);
        } catch (IOException | InterruptedException e) {
            log.error("Error executing Python script", e);
        }
    }

    private void executePythonScriptWithWeight(String scriptPath, List<Float> data,SignalData signalData, String weightPath) {
        try {
            log.info("Executing Python script with weight: {}", scriptPath);
            ProcessBuilder pb = new ProcessBuilder(PYTHON_EXECUTABLE_PATH, scriptPath);
            Process process = pb.start();
            sendPythonDataWithWeight(process, data, weightPath);

            readAIResult(process, signalData);
        } catch (IOException | InterruptedException e) {
            log.error("Error executing Python script", e);
        }
    }

    private void sendPythonData(Process process, List<Float> data, String fileName) throws IOException {
        OutputStream outputStream = process.getOutputStream();
        StringBuilder sb = new StringBuilder();
        sb.append(fileName).append("\n");
        data.forEach(value -> sb.append(value.toString()).append(","));
        sb.deleteCharAt(sb.length() - 1);
        outputStream.write(sb.toString().getBytes());
        outputStream.flush();
        outputStream.close();
    }

    private void sendPythonDataWithWeight(Process process, List<Float> data, String weightPath) throws IOException {
        OutputStream outputStream = process.getOutputStream();
        StringBuilder sb = new StringBuilder();
        sb.append(weightPath).append("\n");
        data.forEach(value -> sb.append(value.toString()).append(","));
        sb.deleteCharAt(sb.length() - 1);
        outputStream.write(sb.toString().getBytes());
        outputStream.flush();
        outputStream.close();
    }

    private void logPythonOutput(Process process) throws IOException, InterruptedException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            log.info("Python output: {}", line);
        }
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while ((line = errorReader.readLine()) != null) {
            log.error("Python error: {}", line);
        }
        int exitCode = process.waitFor();
        log.info("Python script exited with code: {}", exitCode);
    }

    private void readAIResult(Process process, SignalData signalData) throws IOException, InterruptedException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = reader.readLine();
        String[] parts = line.split(" ");
        log.info("AI Result line: {}", line);

        if (parts.length == 2) {
            boolean aiResult = Boolean.parseBoolean(parts[0]);
            float aiProbability = Float.parseFloat(parts[1]);

            signalData.setAiResult(aiResult);
            signalData.setAiProbability(aiProbability);

            service.save(signalData);

            log.info("AI Result: {}", aiResult);
            log.info("AI Probability: {}", aiProbability);
        }

        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String errorLine;
        while ((errorLine = errorReader.readLine()) != null) {
            log.error("Python error: {}", errorLine);
        }

        int exitCode = process.waitFor();
        log.info("Python script exited with code: {}", exitCode);
    }

    private static List<DataPoint> getTimeDataPoint(List<Float> floatList) {
        double eps = 0.1 / 20000;
        List<DataPoint> dataPointList = new ArrayList<>(20000);
        for (int i = 0; i < 20000; i++) {
            dataPointList.add(new DataPoint((i + 1) * eps, floatList.get(i)));
        }
        return dataPointList;
    }

    private static List<DataPoint> getFrequencyDataPoint(String selectedFileName) throws IOException {
        String binFilePath = Paths.get(USER_DIR, selectedFileName).toString();
        DataInputStream dis = new DataInputStream(new FileInputStream(binFilePath));
        List<DataPoint> dataPointList = new ArrayList<>();
        for (int i = 0; i < 2500; i++) {
            float x = (i + 1) * 10;
            double y = ByteUtils.readDoubleLittleEndian(dis);
            dataPointList.add(new DataPoint(x, y));
        }
        return dataPointList;
    }

    private static List<Float> getListByCh(FileInfo fileInfo, int chId) {
        Pair<ChInfo, List<Float>> pairList = fileInfo.getPairList().get(chId);
        log.info("chId ={}" , chId);
        return pairList.getValue();
    }

    private int getChannelId(Integer selectedCh, FileInfo fileInfo) {
        return (selectedCh == null) ? fileInfo.getHeaderInfo().getEvent_Ch() : selectedCh;
    }


}