package br.com.estudo.anderson.quarkussocial.dto;

import br.com.estudo.anderson.quarkussocial.domain.model.Post;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponse {

    private String text;
    private LocalDateTime dateTime;

    public static PostResponse fromEntity(Post post){
        PostResponse response = new PostResponse();
        response.setText(post.getText());
        response.setDateTime(post.getDataTime());
        return response;
    }
}
