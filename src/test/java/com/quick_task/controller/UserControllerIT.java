package com.quick_task.controller;

import com.quick_task.dto.create.CreateWebUserDtoRequest;
import com.quick_task.dto.response.WebUserDtoResponse;
import com.quick_task.dto.update.UpdateWebUserDtoRequest;
import com.quick_task.factory.DaoFactory;
import com.quick_task.security.security_config.MyUserDetails;
import com.quick_task.service.EmailServiceImpl;
import com.quick_task.service.MyUserDetailsService;
import com.quick_task.service.WebUserServiceImpl;
import com.quick_task.utils.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;
import org.testcontainers.utility.DockerImageName;

import java.sql.SQLException;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIT {
    private static final Logger logger = LoggerFactory.getLogger(UserControllerIT.class);

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    @Autowired
    private WebTestClient webTestClient;

    private AutoCloseable closeable;
    @Mock
    private Executor taskExecutor;
    @MockitoBean
    private EmailServiceImpl emailService;
    @Mock
    private WebUserServiceImpl authService;

    @Mock
    private MyUserDetailsService customUserDetailsService;

    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;


    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        logger.info("Connecting to database at URL: {}", postgres.getJdbcUrl());
        logger.info("Database username: {}", postgres.getUsername());
        logger.info("Database password: {}", postgres.getPassword());


        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);


    }

    @BeforeEach
    public void setUp() {
        closeable = Mockito.mockStatic(DaoFactory.class);

        taskExecutor = Mockito.mock(Executor.class);
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));



    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }


    @Test
    void canEstablishedConnection() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void createUser() {
        CreateWebUserDtoRequest request =
                CreateWebUserDtoRequest.builder()
                        .nameUser("testName")
                        .mailUser("test@gmail.com")
                        .password("test")
                        .build();
        webTestClient.post()
                .uri("/registration")
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated();
    }


    @Test
    void updateUser() throws SQLException {
        CreateWebUserDtoRequest createRequest =
                CreateWebUserDtoRequest.builder()
                        .nameUser("testName")
                        .mailUser("test@gmail.com")
                        .password("test")
                        .build();
        webTestClient.post()
                .uri("/registration")
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .bodyValue(createRequest)
                .exchange()
                .expectStatus()
                .isCreated();

        String email = "test@gmail.com";
        var webUser = webTestClient.get()
                .uri(String.format("/user?email=" + email))
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(WebUserDtoResponse.class)
                .returnResult()
                .getResponseBody();

        MyUserDetails userDetails = new MyUserDetails(webUser.getUserId(), webUser.getMailUser(), webUser.getPassword(), null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String accessToken = jwtUtils.generateToken(authentication);
        when(customUserDetailsService.loadUserByUsername("test@gmail.com")).thenReturn(userDetails);

        String mockBearerToken = "Bearer " + accessToken;


        UpdateWebUserDtoRequest updateRequest =
                UpdateWebUserDtoRequest.builder()
                        .nameUser("newTestName")
                        .mailUser("newTestMail@gmail.com")
                        .build();
        webTestClient.patch()
                .uri("/general-user")
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void deleteUser() {
        CreateWebUserDtoRequest request =
                CreateWebUserDtoRequest.builder()
                        .nameUser("testName")
                        .mailUser("test@gmail.com")
                        .password("test")
                        .build();
        webTestClient.post()
                .uri("/registration")
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated();
        String email = "test@gmail.com";
        var webUser = webTestClient.get()
                .uri(String.format("/user?email=" + email))
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(WebUserDtoResponse.class)
                .returnResult()
                .getResponseBody();

        MyUserDetails userDetails = new MyUserDetails(webUser.getUserId(), webUser.getMailUser(), webUser.getPassword(), null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String accessToken = jwtUtils.generateToken(authentication);
        when(customUserDetailsService.loadUserByUsername("test@gmail.com")).thenReturn(userDetails);

        String mockBearerToken = "Bearer " + accessToken;

        webTestClient.delete()
                .uri("/user")
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk();

        var verifyDeleted = webTestClient.get()
                .uri(String.format("/user?email=" + email))
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}