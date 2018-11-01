package br.edu.ulbra.election.election.service;

import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.input.v1.ElectionInput;
import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.output.v1.ElectionOutput;
import br.edu.ulbra.election.election.repository.ElectionRepository;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springfox.documentation.swagger2.mappers.ModelMapper;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ElectionService {
    private final ElectionRepository electionRepository;

    private final ModelMapper modelMapper;

    private static final String MESSAGE_INVALID_ID = "Invalid id";
    private static final String MESSAGE_CANDIDATE_NOT_FOUND = "Candidate not found";


    @Autowired
    public ElectionService(ElectionRepository electionRepository, ModelMapper modelMapper){
        this.electionRepository = electionRepository;
        this.modelMapper = modelMapper;
    }

    public List<ElectionOutput> getAll(){
        Type candidateOutputListType = new TypeToken<List<ElectionOutput>>(){}.getType();
        return modelMapper.map(electionRepository.findAll(), candidateOutputListType);
    }

    private void validateInput(ElectionInput electionInput, boolean isUpdate){

        if(electionInput.getYear() == null){
            throw new GenericOutputException("Invalid year");
        }
        if(electionInput.getStateCode() == null){
            throw new GenericOutputException("Invalid state_code");
        }
        if(electionInput.getDescription() == null){
            throw new GenericOutputException("Invalid description");
        }

    }
}
