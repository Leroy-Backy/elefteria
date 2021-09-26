package org.elefteria.elefteriasn.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.elefteria.elefteriasn.entity.Poll;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PollDto {
    private Long id;
    private String question;
    private List<PollOptionDto> options = new ArrayList<>();

    public PollDto(){}

    public PollDto(Poll poll){
        this.id = poll.getId();
        this.question = poll.getQuestion();

        poll.getOptions().forEach(option -> options.add(new PollOptionDto(option)));
    }
}
