package br.edu.ulbra.election.election.service;

import br.edu.ulbra.election.election.client.CandidateClientService;
import br.edu.ulbra.election.election.model.Vote;
import br.edu.ulbra.election.election.output.v1.ElectionCandidateResultOutput;
import br.edu.ulbra.election.election.output.v1.ResultOutput;
import br.edu.ulbra.election.election.output.v1.VoteOutput;
import br.edu.ulbra.election.election.output.v1.VoterOutput;
import br.edu.ulbra.election.election.repository.ElectionRepository;
import br.edu.ulbra.election.election.repository.VoteRepository;
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

        resultOutput.setNullVotes(nullVotesTotal);
        resultOutput.setBlankVotes(blanckVotesTotal);
        resultOutput.setCandidates(valideVotes);
        resultOutput.setTotalVotes(Long.valueOf(list.size()));
        return resultOutput;
    }

    public ElectionCandidateResultOutput getResultByCandidate(Long candidateId){
        ElectionCandidateResultOutput electionCandidateResultOutput = new ElectionCandidateResultOutput();

        electionCandidateResultOutput.setCandidate(candidateClientService.getById(candidateId));

        electionCandidateResultOutput.setTotalVotes(voteRepository.countVoteByCandidateId(candidateId));
        return electionCandidateResultOutput;
    }
}
