package br.com.estudo.anderson.quarkussocial.rest;

import br.com.estudo.anderson.quarkussocial.domain.model.Follower;
import br.com.estudo.anderson.quarkussocial.domain.model.User;
import br.com.estudo.anderson.quarkussocial.domain.repository.FollowerRepository;
import br.com.estudo.anderson.quarkussocial.domain.repository.UserRepository;
import br.com.estudo.anderson.quarkussocial.dto.FollowRequest;
import br.com.estudo.anderson.quarkussocial.dto.FollowerResponse;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    Long userId;
    Long followerId;

    @BeforeEach
    @Transactional
    void setup(){

        var user = new User();
        user.setAge(30);
        user.setName("Fulano");

        var followerUser = new User();
        user.setAge(35);
        user.setName("Cricano");

        userRepository.persist(user);
        userRepository.persist(followerUser);

        userId = user.getId();
        followerId = followerUser.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(followerUser);

        followerRepository.persist(follower);
    }

    @Test
    @DisplayName("Should return 409 when followerId is equal to User id")
    @Order(1)
    public void sameUserAsFollowerTest() {

        FollowRequest followRequest = new FollowRequest();
        followRequest.setFollowerId(userId);

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .body(followRequest)
        .when()
                .put()
        .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode());
    }

    @Test
    @DisplayName("Should return 404 when User Id doesn't exist")
    @Order(2)
    public void userNotFoundTest() {

        FollowRequest followRequest = new FollowRequest();
        followRequest.setFollowerId(followerId);

        Long inexistentUserId = 100L;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentUserId)
                .body(followRequest)
        .when()
                .put()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should follow a user")
    @Order(3)
    public void followUserTest() {

        FollowRequest followRequest = new FollowRequest();
        followRequest.setFollowerId(followerId);

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .body(followRequest)
        .when()
                .put()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("Should return 404 on list user followers and User id doesn't exist")
    @Order(4)
    public void userNotFoundWhenListingFollowersTest() {

        var inexistentUserId = 100;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentUserId)
        .when()
                .get()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should list a user's followers")
    @Order(5)
    public void listFollowersTest() {

        var response = given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
        .when()
                .get()
        .then()
                .extract().response();

        assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
        assertEquals(1, response.jsonPath().getList("content").size() );
    }

    @Test
    @DisplayName("Should unfollow user")
    @Order(6)
    public void unfollowUserTest() {

        var response = given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .queryParam("followerId", followerId)
        .when()
                .delete()
        .then()
                .extract().response();

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.statusCode());
    }
}