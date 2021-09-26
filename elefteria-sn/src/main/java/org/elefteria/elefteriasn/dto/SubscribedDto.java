package org.elefteria.elefteriasn.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscribedDto {
    private boolean isSubscribed;

    public SubscribedDto(boolean isSubscribed) {
        this.isSubscribed = isSubscribed;
    }
}
