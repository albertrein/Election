package br.edu.ulbra.election.election.api.v1;

import br.edu.ulbra.election.election.input.v1.VoteInput;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import br.edu.ulbra.election.election.service.VoteService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/vote")
public class VoteApi {

    private final VoteService voteService;

    @Autowired
    public VoteApi(VoteService voteService){
        this.voteService = voteService;
    }

    @PostMapping("/")
    public GenericOutput electionVote(@RequestHeader(value = "x-token") String token, @RequestBody VoteInput voteInput){
        return voteService.electionVote(voteInput, token);
    }

    @PostMapping("/multiple")
    public GenericOutput multipleElectionVote(@RequestHeader(value = "x-token") String token, @RequestBody List<VoteInput> voteInputList){
        return voteService.multiple(voteInputList, token);
    }

//    @PutMapping("/")
//    public GenericOutput electionVote(@RequestBody VoteInput voteInput){
//        return voteService.electionVote(voteInput);
//    }
//
//    @PutMapping("/multiple")
//    public GenericOutput multipleElectionVote(@RequestBody List<VoteInput> voteInputList){
//        return voteService.multiple(voteInputList);
//    }

    @GetMapping("/getvote/{electionId}")
    @ApiOperation(value = "Get votes by ElectionId")
    public Long countVotesByElectionId(@PathVariable Long electionId){
        return voteService.countVotesByElectionId(electionId);
    }

    @GetMapping("/voter/{voterId}")
    @ApiOperation(value = "Get count votes by voterId")
    public Long countVotesByVoterId(@PathVariable Long voterId){
        return voteService.countVotesByVoterId(voterId);
    }
}
