package br.com.estudo.anderson.quarkussocial.rest;

import br.com.estudo.anderson.quarkussocial.domain.model.Post;
import br.com.estudo.anderson.quarkussocial.domain.model.User;
import br.com.estudo.anderson.quarkussocial.domain.repository.FollowerRepository;
import br.com.estudo.anderson.quarkussocial.domain.repository.PostRepository;
import br.com.estudo.anderson.quarkussocial.domain.repository.UserRepository;
import br.com.estudo.anderson.quarkussocial.dto.CreatePostRequest;
import br.com.estudo.anderson.quarkussocial.dto.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private UserRepository userRepository;
    private PostRepository repository;
    private FollowerRepository followerRepository;

    @Inject
    public PostResource(UserRepository userRepository, PostRepository repository,
                        FollowerRepository followerRepository) {
        this.userRepository = userRepository;
        this.repository = repository;
        this.followerRepository = followerRepository;
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest postData) {
        User user = userRepository.findById(userId);
        if(user == null) return Response.status(Response.Status.NOT_FOUND).build();

        Post post = new Post();
        post.setText(postData.getText());
        post.setUser(user);
        post.setDataTime(LocalDateTime.now());

        repository.persist(post);

        return Response.status(Response.Status.CREATED).entity(post).build();
    }

    @GET
    public Response listPosts(
            @PathParam("userId") Long userId,
            @HeaderParam("followerId") Long followerId) {

        User user = userRepository.findById(userId);
        if(user == null) return Response.status(Response.Status.NOT_FOUND).build();

        if(followerId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("You forget the header followerId")
                    .build();
        }

        User follower = userRepository.findById(followerId);
        if(follower == null) return Response.status(Response.Status.NOT_FOUND).build();

        boolean follows = followerRepository.follows(follower, user);

        if(!follows){
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You can't see these posts").build();
        }

        //PanacheQuery<Post> posts = repository.findAll();
        PanacheQuery<Post> query = repository.find("user",
                /*Sort.by("datetime", Sort.Direction.Descending),*/ user);

        List<PostResponse> response = query
                .stream()
                .map(post -> PostResponse.fromEntity(post))
                .collect(Collectors.toList());

        // map reference .map(PostResponse::fromEntity).collect(Collectors.toList());

        return Response.ok(response).build();
    }
}
