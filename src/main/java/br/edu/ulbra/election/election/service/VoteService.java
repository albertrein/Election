package br.edu.ulbra.election.election.service;

import br.edu.ulbra.election.election.client.CandidateClientService;
import br.edu.ulbra.election.election.client.LoginClientService;
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
    private final LoginClientService loginClientService;

    @Autowired
    public VoteService(VoteRepository voteRepository, ElectionRepository electionRepository, VoterClientService voterClientService, CandidateClientService candidateClientService, LoginClientService loginClientService){
        this.voteRepository = voteRepository;
        this.electionRepository = electionRepository;
        this.voterClientService = voterClientService;
        this.candidateClientService = candidateClientService;
        this.loginClientService = loginClientService;
    }

    public GenericOutput electionVote(VoteInput voteInput, String token){
        Election election = validateInput(voteInput.getElectionId(), voteInput);

        //Checking token validation
        if(token == null){
            throw new GenericOutputException("Insert Token");
        }
        checkTokenVoter(voteInput.getVoterId(), token);
        Vote vote = new Vote();
        vote.setElection(election);
        vote.setVoterId(voteInput.getVoterId());

        vote.setNullVote(false);
        if (voteInput.getCandidateNumber() == null){
            vote.setBlankVote(true);
        } else {
            vote.setBlankVote(false);
            try {
                candidateClientService.getByNumberAndElection(voteInput.getElectionId(), voteInput.getCandidateNumber());
            } catch (FeignException ex){
                if (ex.status() == 500){
                    vote.setNullVote(true);
                }
            }
        }

        voteRepository.save(vote);

        return new GenericOutput("OK");
    }
    /*
        Vote vote = new Vote();
        vote.setElection(election);
        vote.setVoterId(voteInput.getVoterId());

    //validating voter's vote
        if (voteInput.getCandidateNumber() == null){
            vote.setBlankVote(true);
        } else {
            vote.setBlankVote(false);
        }

        //validating null
        vote.setNullVote(false);
        try{
            Long candidateId = candidateClientService.getCandidateIdByCandidateNumber(voteInput.getCandidateNumber());
            if(candidateId == null){
                vote.setNullVote(true);
            }else{
                vote.setCandidateId(candidateId); //CandidateID receives the candidate ID
            }
        }catch (FeignException e){
            if(e.status() == 0){
                throw new GenericOutputException("Candidate not found");
            }
            if(e.status() == 500){
                throw new GenericOutputException("Invalid Value");
            }
        }
        //end of vote validation
        /*System.out.println("***********************************");
        System.out.println("Voter Id: "+vote.getVoterId());
        System.out.println("VOTO NULO: "+vote.getNullVote());
        System.out.println("VOTO BRANCO: "+vote.getBlankVote());
        System.out.println("VOTO Candidato: "+vote.getCandidateId());
        System.out.println("***********************************");*/
      /*  voteRepository.save(vote);

        return new GenericOutput("OK");
    }
    */


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

    //binding a valid voter and a valid election
        //Validating election
        Election election = electionRepository.findById(electionId).orElse(null);
        if (election == null){
            throw new GenericOutputException("Invalid Election");
        }
        //Validating voter
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
    //End of the voter´s bonding and election

        //Validating if Vote already voted in election
        if(voteRepository.findFirstByVoterIdAndElection(voteInput.getVoterId(), election) != null){
            throw new GenericOutputException("Voter Already Vote in this Election");
        }

        return election;
    }
    public void checkTokenVoter(Long voterId, String token){
        try{
            VoterOutput voterOutput = loginClientService.checkToken(token);
            if(voterOutput == null || voterOutput.getId() != voterId){
                throw new GenericOutputException("Invalid Token");
            }
        }catch (FeignException e){
            System.out.println(e.getMessage());
            if(e.status() == 0){
                throw new GenericOutputException("Login Service Not Found");
            }
            if (e.status() == 500) {
                throw new GenericOutputException("Invalid Login");
            }
        }
    }

}
