package br.edu.ulbra.election.election.service;

import br.edu.ulbra.election.election.client.CandidateClientService;
import br.edu.ulbra.election.election.client.VoterClientService;
import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.input.v1.VoteInput;
import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.model.Vote;
import br.edu.ulbra.election.election.output.v1.CandidateOutput;
import br.edu.ulbra.election.election.output.v1.ElectionOutput;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import br.edu.ulbra.election.election.output.v1.VoterOutput;
import br.edu.ulbra.election.election.repository.ElectionRepository;
import br.edu.ulbra.election.election.repository.VoteRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoteService {

    private final VoteRepository voteRepository;

    private final ElectionRepository electionRepository;
    private final VoterClientService voterClientService;
    private final CandidateClientService candidateClientService;
    @Autowired

    public VoteService(VoteRepository voteRepository, ElectionRepository electionRepository, VoterClientService voterClientService, CandidateClientService candidateClientService){
        this.voteRepository = voteRepository;
        this.electionRepository = electionRepository;
        this.voterClientService = voterClientService;
        this.candidateClientService = candidateClientService;
    }

    public GenericOutput getById(Long id){
        if (id == null){
            throw new GenericOutputException("Invalid Id");
        }

        Vote vote = voteRepository.getVoteById(id);
        GenericOutput genericOutput;
        if (vote == null){
            genericOutput = null;
        }else{
            genericOutput = new GenericOutput(vote.toString());
        }

        return genericOutput;
    }

    public GenericOutput electionVote(VoteInput voteInput){

        Election election = validateInput(voteInput.getElectionId(), voteInput);
        Vote vote = new Vote();
        vote.setElection(election);
        vote.setVoterId(voteInput.getVoterId());

        if (voteInput.getCandidateNumber() == null){
            vote.setBlankVote(true);
        } else {
            vote.setBlankVote(false);
        }

        vote.setNullVote(false);

        try{
            CandidateOutput candidateOutput = candidateClientService.getCandidateByNumberElection(voteInput.getCandidateNumber());
            if(candidateOutput == null){
                vote.setNullVote(true);
            }
        } catch (FeignException e){
            if (e.status() == 500) {
                throw new GenericOutputException("Invalid Voter");
            }
        }

        voteRepository.save(vote);

        return new GenericOutput("OK");
    }

    public GenericOutput multiple(List<VoteInput> voteInputList){
        for (VoteInput voteInput : voteInputList){
            this.electionVote(voteInput);
        }
        return new GenericOutput("OK");
    }

    public Election validateInput(Long electionId, VoteInput voteInput){
        Election election = electionRepository.findById(electionId).orElse(null);
        if (election == null){
            throw new GenericOutputException("Invalid Election");
        }
        if (voteInput.getVoterId() == null){
            throw new GenericOutputException("Invalid Voter");
        }


        try{ // try do feign
            // TODO: Validate voter
            VoterOutput voterOutput = voterClientService.getById(voteInput.getVoterId()); //retornando um voter pelo id
            if(voterOutput == null){ // Se for nulo significa que o voter não existe
                throw new GenericOutputException("Invalid Voter");
            }
        } catch (FeignException e){
            if (e.status() == 500) {
                throw new GenericOutputException("Invalid Voter");
            }
        }


        try{ // try do feign
            VoterOutput voterOutput = voterClientService.getById(voteInput.getVoterId()); //retornando um voter pelo id
            if(voterOutput == null){ // Se for nulo significa que o voter não existe
                throw new GenericOutputException("Invalid Voter");
            }
        } catch (FeignException e){
            if (e.status() == 500) {
                throw new GenericOutputException("Invalid Voter");
            }
        }

        if(electionRepository.getElectionById(voteInput.getElectionId()) == null){ //Retornando uma eleição pelo id, verificando se uma elição existe
            throw new GenericOutputException("Invalid Election");
        }


        return election;
    }
}
