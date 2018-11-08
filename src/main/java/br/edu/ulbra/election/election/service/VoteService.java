package br.edu.ulbra.election.election.service;

import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.input.v1.VoteInput;
import br.edu.ulbra.election.election.model.Vote;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import br.edu.ulbra.election.election.repository.VoteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoteService {

    private VoteRepository voteRepository;

    private final ModelMapper modelMapper;

    private static final String MESSAGE_INVALID_ID = "Invalid id";
    private static final String MESSAGE_ELECTION_NOT_FOUND = "Voter not found";

    @Autowired
    public VoteService(VoteRepository voteRepository, ModelMapper modelMapper){ //constructor
        this.voteRepository = voteRepository;
        this.modelMapper = modelMapper;
    }

    public GenericOutput create(VoteInput electionInput) {
        validateInput(electionInput);
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        Vote vote = modelMapper.map(electionInput, Vote.class);
        vote = voteRepository.save(vote);
        return new GenericOutput("OK");
    }

    public void validateInput(VoteInput voteInput) {
        if (voteInput.getCandidateId() == null) {
            throw new GenericOutputException("Invalid CandidateId");
        }

        if (voteInput.getElectionId() == null) {
            throw new GenericOutputException("Invalid ElectionId");
        }

        if (voteInput.getVoterId() == null) {
            throw new GenericOutputException("Invalid Voter");
        }

    }

}
