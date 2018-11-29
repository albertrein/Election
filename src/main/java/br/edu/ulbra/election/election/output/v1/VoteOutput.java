package br.edu.ulbra.election.election.output.v1;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Vote Output information")
public class VoteOutput {

    @ApiModelProperty(example = "1", notes = "Vote Unique Identification")
    private Long id;

    @ApiModelProperty(example = "2", notes = "Voter Id")
    private Long voterId;

    @ApiModelProperty(example = "3", notes = "Candidate Id")
    private Long candidateId;

    @ApiModelProperty(example = "true", notes = "Blank vote")
    private Boolean blankVote;

    @ApiModelProperty(example = "true", notes = "Null vote")
    private Boolean nullVote;

    @ApiModelProperty(notes = "Election data")
    private ElectionOutput election;

    public Long getVoterId() {
        return voterId;
    }

    public void setVoterId(Long voterId) {
        this.voterId = voterId;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public Boolean getBlankVote() {
        return blankVote;
    }

    public void setBlankVote(Boolean blankVote) {
        this.blankVote = blankVote;
    }

    public Boolean getNullVote() {
        return nullVote;
    }

    public void setNullVote(Boolean nullVote) {
        this.nullVote = nullVote;
    }

    public ElectionOutput getElection() {
        return election;
    }

    public void setElection(ElectionOutput election) {
        this.election = election;
    }
}
