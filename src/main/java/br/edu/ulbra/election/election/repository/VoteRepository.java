package br.edu.ulbra.election.election.repository;

import br.edu.ulbra.election.election.model.Vote;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import org.springframework.data.repository.CrudRepository;

public interface VoteRepository extends CrudRepository<Vote, Long> {
    Long countByElection_Id(Long election_id);
    Long countByVoterId(Long voterId);
}
