package br.edu.ulbra.election.election.client;

import br.edu.ulbra.election.election.output.v1.CandidateOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class CandidateClientService {
    private final CandidateClient candidateClient;

    @Autowired
    public CandidateClientService(CandidateClient candidateClient) {
        this.candidateClient = candidateClient;
    }

    public Long getCountElectionById(Long electionId){
        return this.candidateClient.getCountElectionById(electionId);
    }

    public Long getCandidateIdByCandidateNumber(Long candidateNumber){
        return this.candidateClient.getCandidateIdByCandidateNumber(candidateNumber);
    }

    public CandidateOutput getById(Long candidateId){
        return this.candidateClient.getById(candidateId);
    }

    public CandidateOutput getByNumberAndElection(Long electionId, Long candidateNumber){
        return candidateClient.getByNumberAndElection(electionId, candidateNumber);
    }

    @FeignClient(value="candidate-service", url="http://localhost:8082")
    public interface CandidateClient{

        @GetMapping("/v1/candidate/election/{electionId}")
        Long getCountElectionById(@PathVariable(name = "electionId") Long electionId);

        @GetMapping("/v1/candidate/{candidateId}")
        CandidateOutput getById(@PathVariable(name = "candidateId") Long candidateId);

        @GetMapping("/v1/candidate/candidatenumber/{candidateNumber}")
        Long getCandidateIdByCandidateNumber(@PathVariable(name = "candidateNumber") Long candidateNumber);

        @GetMapping("/getByNumberAndElection/{electionId}/{candidateNumber}")
        CandidateOutput getByNumberAndElection(@PathVariable(name = "electionId") Long electionId, @PathVariable(name = "candidateNumber") Long candidateNumber);

    }
}
