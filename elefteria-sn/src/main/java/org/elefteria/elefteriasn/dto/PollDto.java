package org.elefteria.elefteriasn.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.elefteria.elefteriasn.entity.Poll;

import java.util.ArrayList;
import java.util.Comparator;
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
        // sort by number order
        options.sort(new Comparator<PollOptionDto>() {
            @Override
            public int compare(PollOptionDto o1, PollOptionDto o2) {
                if(o1.getNumber() < o2.getNumber()) return -1;
                if(o1.getNumber() > o2.getNumber()) return 1;
                return 0;
            }
        });
    }
}
