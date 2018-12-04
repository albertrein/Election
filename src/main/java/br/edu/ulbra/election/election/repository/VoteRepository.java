package br.edu.ulbra.election.election.repository;

import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.model.Vote;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import br.edu.ulbra.election.election.output.v1.VoteOutput;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface VoteRepository extends CrudRepository<Vote, Long> {
    Long countByElection_Id(Long election_id);
    Long countByVoterId(Long voterId);
    List<Vote> getAllByElection_Id(Long election_id);
    Long countVoteByCandidateId(Long candidateId);
    Vote findFirstByVoterIdAndElection(Long voterId, Election election);
}
