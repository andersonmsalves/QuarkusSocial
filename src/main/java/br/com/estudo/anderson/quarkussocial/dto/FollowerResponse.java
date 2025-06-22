package br.com.estudo.anderson.quarkussocial.dto;

import br.com.estudo.anderson.quarkussocial.domain.model.Follower;
import lombok.Data;

@Data
public class FollowerResponse {

    private Long id;
    private String name;

    public FollowerResponse(){

    }

    public FollowerResponse(Follower follower){
        this.id = follower.getId();
        this.name = follower.getFollower().getName();
    }
}
