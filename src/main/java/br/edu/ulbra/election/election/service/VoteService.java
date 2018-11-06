package br.edu.ulbra.election.election.service;

import br.edu.ulbra.election.election.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoteService {
    @Autowired
    private VoteRepository voteRepository;


}
