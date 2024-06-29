package lpms.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lpms.backend.entity.SignalData;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.List;

import static lpms.backend.utils.FileUtils.removeBinExtension;
import static lpms.backend.utils.ProjectConstans.*;
import static lpms.backend.utils.ProjectConstans.PYTHON_EXECUTABLE_PATH;

@Component
@RequiredArgsConstructor
@Slf4j
public class PythonScriptExecutor {

    private final SignalDataService service;

    /**
     * Performs Fast Fourier Transform (FFT) on the data and saves the result.
     *
     * @param data the list of float values representing the data
     * @param fileName the name of the file
     */
    public void performFFT(List<Float> data, String fileName) throws IOException, InterruptedException {
        String pythonScriptPath = Paths.get(PYTHON_SCRIPT_PATH, "fft.py").toString();
        executePythonScript(pythonScriptPath, data, removeBinExtension(fileName));
    }

    /**
     * Performs Short-Time Fourier Transform (STFT) and AI analysis on the data.
     *
     * @param data the list of float values representing the data
     * @param eventData the list of float values representing the event data
     * @param signalData the SignalData entity
     */
    public void performSTFTAndAI(List<Float> data,List<Float> eventData, SignalData signalData) throws Exception {
        String stftScriptPath = Paths.get(PYTHON_SCRIPT_PATH, "stft.py").toString();
        String aiScriptPath = Paths.get(PYTHON_SCRIPT_PATH, "ai.py").toString();
        String weightPath = Paths.get(ROOT_DIR, "50_0.013085763479087876.pth").toString();
        executePythonScript(stftScriptPath, data, removeBinExtension(signalData.getFileName()));
        if(signalData.getAiProbability()==null)executePythonScriptWithWeight(aiScriptPath, eventData, signalData, weightPath);
    }

    public void performSTFTAndAIV2(List<Float> data,String fileName, SignalData signalData) throws Exception {
        String stftScriptPath = Paths.get(PYTHON_SCRIPT_PATH, "stft.py").toString();
        String aiScriptPath = Paths.get(PYTHON_SCRIPT_PATH, "ai.py").toString();
        String weightPath = Paths.get(ROOT_DIR, "50_0.013085763479087876.pth").toString();
        String csvFilePath = Paths.get(ROOT_DIR, "lpms_binary_data_csv",removeBinExtension(signalData.getFileName())+".csv").toString();
        log.info("csvFilePath = {}", csvFilePath);
        executePythonScript(stftScriptPath, data, removeBinExtension(signalData.getFileName()));

        if(signalData.getAiProbability()==null)executePythonScriptWithWeightV2(aiScriptPath,  csvFilePath, signalData, weightPath);
    }

    /**
     * Executes a Python script with the specified data and file name.
     *
     * @param scriptPath the path to the Python script
     * @param data the list of float values to be passed to the script
     * @param fileName the name of the file
     */
    public void executePythonScript(String scriptPath, List<Float> data, String fileName) throws IOException, InterruptedException {

        log.info("Executing Python script: {}", scriptPath);
        ProcessBuilder pb = new ProcessBuilder(PYTHON_EXECUTABLE_PATH, scriptPath);
        Process process = pb.start();
        sendPythonData(process, data, fileName);
        logPythonOutput(process);

    }

    /**
     * Executes a Python script with the specified data, SignalData entity, and weight path.
     *
     * @param scriptPath the path to the Python script
     * @param data the list of float values to be passed to the script
     * @param signalData the SignalData entity
     * @param weightPath the path to the weight file
     */
    public void executePythonScriptWithWeight(String scriptPath, List<Float> data,SignalData signalData, String weightPath) throws Exception {

        log.info("Executing Python script with weight: {}", scriptPath);
        ProcessBuilder pb = new ProcessBuilder(PYTHON_EXECUTABLE_PATH, scriptPath);
        Process process = pb.start();
        sendPythonDataWithWeight(process, data, weightPath);

        readAIResult(process, signalData);

    }

    public void executePythonScriptWithWeightV2(String scriptPath,String csvFilePath,SignalData signalData, String weightPath) throws Exception {

        log.info("Executing Python script with weight: {}", scriptPath);
        ProcessBuilder pb = new ProcessBuilder(PYTHON_EXECUTABLE_PATH, scriptPath);
        Process process = pb.start();
        sendPythonDataWithWeightV2(process, csvFilePath, weightPath);
        readAIResult(process, signalData);

    }

    /**
     * Sends data to the Python script.
     *
     * @param process the process of the Python script
     * @param data the list of float values to be sent
     * @param fileName the name of the file
     * @throws IOException if an I/O error occurs
     */
    public void sendPythonData(Process process, List<Float> data, String fileName) throws IOException {
        OutputStream outputStream = process.getOutputStream();
        StringBuilder sb = new StringBuilder();
        sb.append(fileName).append("\n");
        data.forEach(value -> sb.append(value.toString()).append(","));
        sb.deleteCharAt(sb.length() - 1);
        outputStream.write(sb.toString().getBytes());
        outputStream.flush();
        outputStream.close();
    }

    /**
     * Sends data and weight path to the Python script.
     *
     * @param process the process of the Python script
     * @param data the list of float values to be sent
     * @param weightPath the path to the weight file
     * @throws IOException if an I/O error occurs
     */
    public void sendPythonDataWithWeight(Process process, List<Float> data, String weightPath) throws IOException {
        OutputStream outputStream = process.getOutputStream();
        StringBuilder sb = new StringBuilder();
        sb.append(weightPath).append("\n");
        data.forEach(value -> sb.append(value.toString()).append(","));
        sb.deleteCharAt(sb.length() - 1);
        outputStream.write(sb.toString().getBytes());
        outputStream.flush();
        outputStream.close();
    }

    public void sendPythonDataWithWeightV2(Process process, String csvFilePath, String weightPath) throws IOException {
        OutputStream outputStream = process.getOutputStream();
        StringBuilder sb = new StringBuilder();
        log.info("send data = {}, {}", weightPath, csvFilePath);
        sb.append(weightPath).append("\n");
        sb.append(csvFilePath).append("\n");
        sb.deleteCharAt(sb.length() - 1);
        log.info("sb = {}", sb);
        outputStream.write(sb.toString().getBytes());
        outputStream.flush();
        outputStream.close();
        log.info ("send data success");
    }

    /**
     * Logs the output from the Python script.
     *
     * @param process the process of the Python script
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted while waiting for the process to finish
     */
    public void logPythonOutput(Process process) throws IOException, InterruptedException {
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

    /**
     * Reads the AI result from the Python script and updates the SignalData entity.
     *
     * @param process the process of the Python script
     * @param signalData the SignalData entity to update
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted while waiting for the process to finish
     */
    public void readAIResult(Process process, SignalData signalData) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = reader.readLine();
        log.info("line = {}", line);
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
}
