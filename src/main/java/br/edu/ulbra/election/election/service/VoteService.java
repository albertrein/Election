package br.edu.ulbra.election.election.service;

import br.edu.ulbra.election.election.client.CandidateClientService;
import br.edu.ulbra.election.election.client.VoterClientService;
import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.input.v1.VoteInput;
import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.model.Vote;
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

    public GenericOutput electionVote(VoteInput voteInput){
        Election election = validateInput(voteInput.getElectionId(), voteInput);
        Vote vote = new Vote();
        vote.setElection(election);
        vote.setVoterId(voteInput.getVoterId());

    //Validando voto do eleitor
        if (voteInput.getCandidateNumber() == null){
            vote.setBlankVote(true);
        } else {
            vote.setBlankVote(false);
        }

        //Validando voto nulo
        vote.setNullVote(false);
        try{
            if(candidateClientService.getCandidateByCandidateNumber(voteInput.getCandidateNumber()) == 0){
                vote.setNullVote(true);
            }
        }catch (FeignException e){
            if(e.status() == 0){
                throw new GenericOutputException("Candidate not found");
            }
            if(e.status() == 500){
                throw new GenericOutputException("Valor Invalido");
            }
        }
        //Fim da validação do voto
        System.out.println("***********************************");
        System.out.println("Voter Id: "+vote.getVoterId());
        System.out.println("VOTO NULO: "+vote.getNullVote());
        System.out.println("VOTO BRANCO: "+vote.getBlankVote());
        System.out.println("***********************************");

        voteRepository.save(vote);

        return new GenericOutput("OK");
    }

    public GenericOutput multiple(List<VoteInput> voteInputList){
        for (VoteInput voteInput : voteInputList){
            this.electionVote(voteInput);
        }
        return new GenericOutput("OK");
    }

    public Long countVotesByElectionId(Long electionId){
        if(electionId == null){
            throw new GenericOutputException("Invalid ElectionId");
        }
        return voteRepository.countByElection_Id(electionId);
    }

    public Long countVotesByVoterId(Long voterId){
        if(voterId == null){
            throw new GenericOutputException("Invalid Id");
        }
        return voteRepository.countByVoterId(voterId);
    }

    public Election validateInput(Long electionId, VoteInput voteInput){
        if (voteInput.getVoterId() == null){
            throw new GenericOutputException("Invalid Voter");
        }

    //Vinculando um eleitor válido e uma eleição válida
        //Validando Eleição
        Election election = electionRepository.findById(electionId).orElse(null);
        if (election == null){
            throw new GenericOutputException("Invalid Election");
        }
        //Validando Eleitor[Voter]
        try{
            VoterOutput voterOutput = voterClientService.getById(voteInput.getVoterId());
            if(voterClientService.getById(voteInput.getVoterId()) == null){
                throw new GenericOutputException("Este eleitor não existe");
            }
        }catch (FeignException e){
            if(e.status() == 0){
                throw new GenericOutputException("Voter Service Not Found");
            }
            if (e.status() == 500) {
                throw new GenericOutputException("Invalid Voter");
            }
        }
    //Fim da vinculação do eleitor e eleição

        return election;
    }
}
