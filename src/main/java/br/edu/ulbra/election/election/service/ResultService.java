package br.edu.ulbra.election.election.service;

import br.edu.ulbra.election.election.client.CandidateClientService;
import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.model.Vote;
import br.edu.ulbra.election.election.output.v1.*;
import br.edu.ulbra.election.election.repository.ElectionRepository;
import br.edu.ulbra.election.election.repository.VoteRepository;
import feign.FeignException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResultService {
    private final ElectionRepository electionRepository;
    private final VoteRepository voteRepository;
    private final CandidateClientService candidateClientService;
    private final ModelMapper modelMapper;

    public ResultService(ElectionRepository electionRepository, VoteRepository voteRepository, CandidateClientService candidateClientService, ModelMapper modelMapper) {
        this.electionRepository = electionRepository;
        this.voteRepository = voteRepository;
        this.candidateClientService = candidateClientService;
        this.modelMapper = modelMapper;
    }

    public ResultOutput getResultByElection(Long electionId){
        ResultOutput resultOutput = new ResultOutput();
        if(electionId == null){
            throw new GenericOutputException("Invalid Election Id");
        }
        Election election = electionRepository.findElectionById(electionId);

        List<Vote> list = voteRepository.getAllByElection_Id(electionId);
        List<ElectionCandidateResultOutput> valideVotes = new ArrayList<>();
        long blanckVotesTotal = 0, nullVotesTotal = 0;

        ArrayList<Long> candidatesVerify = new ArrayList<>();

        for(Vote vote : list){
            if(vote.getCandidateId() == null){
                if(vote.getBlankVote()) {
                    blanckVotesTotal++;
                }
                if(vote.getNullVote()) {
                    nullVotesTotal++;
                }
            }else{
                if(!candidatesVerify.contains(vote.getCandidateId())){
                    valideVotes.add(getResultByCandidate(vote.getCandidateId()));
                    candidatesVerify.add(vote.getCandidateId());
                }
            }
        }
        resultOutput.setElection(modelMapper.map(election, ElectionOutput.class));
        resultOutput.setNullVotes(nullVotesTotal);
        resultOutput.setBlankVotes(blanckVotesTotal);
        resultOutput.setCandidates(valideVotes);
        resultOutput.setTotalVotes((long) list.size());
        return resultOutput;
    }

    public ElectionCandidateResultOutput getResultByCandidate(Long candidateId){
        ElectionCandidateResultOutput electionCandidateResultOutput = new ElectionCandidateResultOutput();
        if(candidateId == null){
            throw new GenericOutputException("Invalid candidateId");
        }

        try{
            electionCandidateResultOutput.setCandidate(candidateClientService.getById(candidateId));
        }catch (FeignException e){
            if(e.status() == 500){
                throw new GenericOutputException(e.getMessage());
            }else if(e.status() == 0){
                throw new GenericOutputException("Candidate Not Found");
            }
        }

        electionCandidateResultOutput.setTotalVotes(voteRepository.countVoteByCandidateId(candidateId));
        return electionCandidateResultOutput;
    }
}
