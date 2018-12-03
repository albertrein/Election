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

    public GenericOutput electionVote(VoteInput voteInput, String token){
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
            Long candidateId = candidateClientService.getCandidateIdByCandidateNumber(voteInput.getCandidateNumber());
            if(candidateId == null){
                vote.setNullVote(true);
            }else{
                vote.setCandidateId(candidateId); //CandidateID recebe o ID do candidato
            }
        }catch (FeignException e){
            if(e.status() == 0){
                throw new GenericOutputException("Candidate not found");
            }
            if(e.status() == 500){
                throw new GenericOutputException("Invalid Value");
            }
        }
        //Fim da validação do voto
        System.out.println("***********************************");
        System.out.println("Voter Id: "+vote.getVoterId());
        System.out.println("VOTO NULO: "+vote.getNullVote());
        System.out.println("VOTO BRANCO: "+vote.getBlankVote());
        System.out.println("VOTO Candidato: "+vote.getCandidateId());
        System.out.println("***********************************");

        voteRepository.save(vote);

        return new GenericOutput("OK");
    }

    public GenericOutput multiple(List<VoteInput> voteInputList, String token){
        for (VoteInput voteInput : voteInputList){
            this.electionVote(voteInput, token);
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
                throw new GenericOutputException("Voter not exists");
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
