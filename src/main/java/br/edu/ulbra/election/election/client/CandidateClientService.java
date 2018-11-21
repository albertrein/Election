package br.edu.ulbra.election.election.client;

import br.edu.ulbra.election.election.output.v1.CandidateOutput;
import br.edu.ulbra.election.election.output.v1.VoterOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class CandidateClientService {
    private final CandidateClient candidateClient;

    @Autowired
    public CandidateClientService(CandidateClient candidateClient){
        this.candidateClient = candidateClient;
    }

    public CandidateOutput getCandidateByElectionId(Long id) {
        return this.candidateClient.getCandidateByElectionId(id);
    }

    @FeignClient(value="candidate-service", url="http://localhost:8082")
    private interface CandidateClient {

        @GetMapping("/v1/candidate/{electionId}")
        CandidateOutput getCandidateByElectionId(@PathVariable(name = "candidateId") Long electionId);
    }
}
