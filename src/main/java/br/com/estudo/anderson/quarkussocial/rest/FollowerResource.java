package br.com.estudo.anderson.quarkussocial.rest;

import br.com.estudo.anderson.quarkussocial.domain.model.Follower;
import br.com.estudo.anderson.quarkussocial.domain.repository.FollowerRepository;
import br.com.estudo.anderson.quarkussocial.domain.repository.UserRepository;
import br.com.estudo.anderson.quarkussocial.dto.FollowRequest;
import br.com.estudo.anderson.quarkussocial.dto.FollowerPerUserResponse;
import br.com.estudo.anderson.quarkussocial.dto.FollowerResponse;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private FollowerRepository repository;
    private UserRepository userRepository;

    @Inject
    public FollowerResource(FollowerRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, FollowRequest request) {
        if(userId.equals(request.getFollowerId() ) ) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Youn can't follow yourself").build();
        }

        var user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var userFollower = userRepository.findById(request.getFollowerId() );
        if(userFollower == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        boolean follows = repository.follows(userFollower, user);

        if(!follows){
            var entity = new Follower();
            entity.setUser(user);
            entity.setFollower(userFollower);
            repository.persist(entity);
            //return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    public Response listFollowers( @PathParam("userId") Long userId ) {

        var user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var list = repository.findByUser(userId);

        FollowerPerUserResponse responseObject = new FollowerPerUserResponse();
        responseObject.setFollowersCount(list.size());
        var followers = list.stream().map(FollowerResponse::new).collect(Collectors.toList());

        responseObject.setContent(followers);
        return Response.ok(responseObject).build();
    }

    @DELETE
    @Transactional
    public Response unfollowUser(
            @PathParam("userId") Long userId,
            @QueryParam("followerId") Long followerId) {

        var user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        repository.deleteByFollowerAndUSer(followerId, userId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
