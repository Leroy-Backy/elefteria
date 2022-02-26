package org.elefteria.elefteriasn.dto;

import lombok.Data;
import org.elefteria.elefteriasn.entity.PollOption;

import java.util.HashSet;
import java.util.Set;

@Data
public class PollOptionDto {
    private int number;
    private String option;
    private Set<String> votes = new HashSet<>();

    public PollOptionDto(){}

    public PollOptionDto(PollOption pollOption){
        this.number = pollOption.getNumber();
        this.option = pollOption.getOption();
        pollOption.getVotes().forEach(user -> votes.add(user.getUsername()));
    }
}
