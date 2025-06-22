package br.com.estudo.anderson.quarkussocial.rest;

import br.com.estudo.anderson.quarkussocial.domain.model.Follower;
import br.com.estudo.anderson.quarkussocial.domain.model.Post;
import br.com.estudo.anderson.quarkussocial.domain.model.User;
import br.com.estudo.anderson.quarkussocial.domain.repository.FollowerRepository;
import br.com.estudo.anderson.quarkussocial.domain.repository.PostRepository;
import br.com.estudo.anderson.quarkussocial.domain.repository.UserRepository;
import br.com.estudo.anderson.quarkussocial.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.net.URL;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    @Inject
    PostRepository postRepository;

    Long userId;
    Long userNotFollowsId;
    Long userFollowerId;

    @BeforeEach
    @Transactional
    public void setup() {
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");

        Post post = new Post();
        post.setText("Some text");
        post.setDataTime(LocalDateTime.now());
        post.setUser(user);

        postRepository.persist(post);

        var user2 = new User();
        user2.setAge(34);
        user2.setName("Cicrano");

        var user3 = new User();
        user3.setAge(40);
        user3.setName("Beltrano");

        userRepository.persist(user);
        userId = user.getId();

        userRepository.persist(user2);
        userNotFollowsId = user2.getId();

        userRepository.persist(user3);
        userFollowerId = user3.getId();

        Follower follower = new Follower();
        follower.setFollower(user3);
        follower.setUser(user);

        followerRepository.persist(follower);
    }

    @Test
    @DisplayName("should create a post for a user")
    public void createPostTest() {

        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        //var userId = 1;

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", userId)
        .when()
                .post()
        .then()
                .statusCode(201);

    }

    @Test
    @DisplayName("should return 404 when trying to make a post for an inexistent user")
    public void postForAnInexistentUserTest() {

        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        var inexistentUserId = 100;

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", inexistentUserId)
        .when()
                .post()
        .then()
                .statusCode(404);

    }

    @Test
    @DisplayName("Should return 404 when trying to retrieve post for an inexistente user")
    public void listPostForUserNotFoundTest() {

        var inexistentUserId = 100;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentUserId)
        .when()
                .get()
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should return 400 when followerId is not present in header")
    public void listPostForUserWithoutFollwerIdTest() {

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
        .when()
                .get()
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should return 404 when followerId does not found in DB")
    public void listPostForUserByInexistentFollower() {

        var followerId = 100;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .header("followerId", followerId)
        .when()
                .get()
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should return 403 when follwerId not follow the user")
    public void listPostNotAFollower(){

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .header("followerId", userNotFollowsId)
        .when()
                .get()
        .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Should return list user's posts to follower")
    public void listUserPostsToAFollower() {

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .header("followerId", userFollowerId)
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }
}