package lpms.backend.service;

import lombok.RequiredArgsConstructor;
import lpms.backend.entity.BinaryData;
import lpms.backend.entity.SignalData;
import lpms.backend.repository.SignalDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class SignalDataService {

    private final SignalDataRepository repository;

    public List<SignalData> findAll(){
        return repository.findAll();
    }
    public SignalData findById(Long signalDataId){
        Optional<SignalData> signalData = repository.findById(signalDataId);
        return signalData.orElse(null);
    }

    public Long save(SignalData signalData){
        repository.save(signalData);
        return signalData.getId();
    }

    public List<BinaryData> findAllBinaryData(){
        List<SignalData> signalDataList = repository.findAll();
        List<BinaryData> binaryDataList = new ArrayList<>();
        for (SignalData signalData : signalDataList) {
            binaryDataList.add(signalData.getBinaryData());
        }
        return binaryDataList;
    }

}
