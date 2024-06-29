package lpms.backend.service;

import lombok.RequiredArgsConstructor;
import lpms.backend.entity.SignalData;
import lpms.backend.repository.SignalDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing SignalData entities.
 * Provides methods for finding, saving, and retrieving SignalData.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class SignalDataService {

    private final SignalDataRepository repository;


    /**
     * Find a SignalData entity by its ID.
     *
     * @param signalDataId the ID of the SignalData entity
     * @return the SignalData entity if found, or null if not found
     */
    public SignalData findById(Long signalDataId){
        Optional<SignalData> signalData = repository.findById(signalDataId);
        return signalData.orElse(null);
    }

    /**
     * Save a SignalData entity to the repository.
     *
     * @param signalData the SignalData entity to save
     * @return the ID of the saved SignalData entity
     */
    public Long save(SignalData signalData){
        repository.save(signalData);
        return signalData.getId();
    }


    /**
     * Find a SignalData entity by its file name.
     *
     * @param fileName the name of the file
     * @return the SignalData entity associated with the given file name
     */
    public SignalData findByFileName(String fileName){
        return repository.findByFileName(fileName);
    }


    /**
     * Retrieve all SignalData entities from the repository.
     *
     * @return a list of all SignalData entities
     */
    public List<SignalData>findAll(){
        return repository.findAll();

    }



}
