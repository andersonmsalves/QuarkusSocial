package br.com.estudo.anderson.quarkussocial.rest;

import br.com.estudo.anderson.quarkussocial.domain.model.User;
import br.com.estudo.anderson.quarkussocial.domain.repository.UserRepository;
import br.com.estudo.anderson.quarkussocial.dto.CreateUserRequest;
import br.com.estudo.anderson.quarkussocial.dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Set;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private UserRepository repository;
    private Validator validator;

    @Inject
    public UserResource(UserRepository repository, Validator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @POST
    @Transactional
    public Response createUser(CreateUserRequest userRequest){

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);
        if(!violations.isEmpty()) {
//            ConstraintViolation<CreateUserRequest> erro = violations.stream().findAny().get();
//            String errorMEssage = erro.getMessage();
            ResponseError responseError = ResponseError.createFromValidation(violations);

            //return Response.status(422).entity(responseError).build();
            return responseError
                    .withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }

        User user = new User();
        user.setAge(userRequest.getAge());
        user.setName(userRequest.getName());

        //user.persist();
        repository.persist(user);

        return Response
                .status(Response.Status.CREATED.getStatusCode())
                .entity(user)
                .build();
    }

    @GET
    public Response listAllUsers() {

        //        PanacheQuery<PanacheEntityBase> query = User.findAll();
        //        return Response.ok(query.list()).build();

        PanacheQuery<User> users = repository.findAll();
        return Response.ok(users.list()).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        User user = repository.findById(id); //User.findById(id);

        if(user != null) {
            //user.delete();
            repository.delete(user);
            return Response
                    .status(Response.Status.NO_CONTENT.getStatusCode())
                    .build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userData) {
        User user = repository.findById(id); //User.findById(id);

        if(user != null) {
            user.setName(userData.getName());
            user.setAge(userData.getAge());
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
