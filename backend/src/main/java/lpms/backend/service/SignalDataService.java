package lpms.backend.service;

import lombok.RequiredArgsConstructor;
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

    public SignalData findById(Long signalDataId){
        Optional<SignalData> signalData = repository.findById(signalDataId);
        return signalData.orElse(null);
    }

    public Long save(SignalData signalData){
        repository.save(signalData);
        return signalData.getId();
    }

    public SignalData findByFileName(String fileName){
        return repository.findByFileName(fileName);
    }

    public List<String> findAllFileNames(){
        return repository.findAllFileNames();
    }

    public List<SignalData>findAll(){
        return repository.findAll();

    }



}
