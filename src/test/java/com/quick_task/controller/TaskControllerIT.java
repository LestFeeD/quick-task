package com.quick_task.controller;

import com.quick_task.dao.TaskDAO;
import com.quick_task.dao.WebUserDAO;
import com.quick_task.dto.create.CreateTaskDtoRequest;
import com.quick_task.dto.response.TaskDtoResponse;
import com.quick_task.dto.update.UpdateTaskDtoRequest;
import com.quick_task.entity.WebUser;
import com.quick_task.factory.DaoFactory;
import com.quick_task.security.security_config.MyUserDetails;
import com.quick_task.service.MyUserDetailsService;
import com.quick_task.service.TaskServiceImpl;
import com.quick_task.service.WebUserServiceImpl;
import com.quick_task.utils.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerIT {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    private static final Logger logger = LoggerFactory.getLogger(TaskControllerIT.class);

    @Autowired
    private WebTestClient webTestClient;
    @Mock
    private WebUserDAO webUserDAO;
    @Mock
    private TaskDAO taskDAO;
    private AutoCloseable closeable;
    @Mock
    private Executor taskExecutor;
    @Mock
    private WebUserServiceImpl authService;

    @Mock
    private MyUserDetailsService customUserDetailsService;

    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;



    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
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
        when(DaoFactory.getDao(WebUserDAO.class)).thenReturn(webUserDAO);
        when(DaoFactory.getDao(TaskDAO.class)).thenReturn(taskDAO);


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
    void allTasks() {
    }

    @Test
    void informationTask() {
    }

    @Test
    void allTasksWithoutProject() {
    }

    @Test
    void allTaskBoard() {
    }

    @Test
    void allTaskProject() {
    }

    @Test
    void changeTask() {
    }


    @Test
    void createTask_WithValidData_ReturnCreatedTask() {

        WebUser mockUser = WebUser.builder()
                .idWebUser(1L)
                .nameUser("uiui")
                .mailUser("afaf")
                .passwordUser("123123")
                .build();
        MyUserDetails userDetails = new MyUserDetails(mockUser.getIdWebUser(), mockUser.getMailUser(), mockUser.getPasswordUser(), null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String accessToken = jwtUtils.generateToken(authentication);
        when(customUserDetailsService.loadUserByUsername("afaf")).thenReturn(userDetails);

        String mockBearerToken = "Bearer " + accessToken;

        CreateTaskDtoRequest request =
                CreateTaskDtoRequest.builder()
                        .nameTask("test")
                        .build();
        webTestClient.post()
                .uri("/task")
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated();

        var allTasksResponse = webTestClient.get()
                .uri("/tasks-user")
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(TaskDtoResponse.class)
                .returnResult()
                .getResponseBody();
        TaskDtoResponse taskCreated = Objects.requireNonNull(allTasksResponse)
                .stream()
                .filter(task -> task.getNameTask().equals(request.getNameTask()))
                .reduce((first, second) -> second)
                .orElseThrow(() -> new AssertionError("Task was not created"));
        assertThat(taskCreated.getNameTask()).isEqualTo(request.getNameTask());

    }

    @Test
    void changeProject() {
    }

    @Test
    void updateTask_WithUpdatedNameAndNewDescription_ReturnUpdatedTask() {
        WebUser mockUser = WebUser.builder()
                .idWebUser(1L)
                .nameUser("uiui")
                .mailUser("afaf")
                .passwordUser("123123")
                .build();

        MyUserDetails userDetails = new MyUserDetails(mockUser.getIdWebUser(), mockUser.getMailUser(), mockUser.getPasswordUser(), null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String accessToken = jwtUtils.generateToken(authentication);
        when(customUserDetailsService.loadUserByUsername("afaf")).thenReturn(userDetails);

        String mockBearerToken = "Bearer " + accessToken;

        CreateTaskDtoRequest request =
                CreateTaskDtoRequest.builder()
                        .nameTask("testIT")
                        .build();
        webTestClient.post()
                .uri("/task")
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated();

        var allTasksResponse = webTestClient.get()
                .uri("/tasks-user")
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(TaskDtoResponse.class)
                .returnResult()
                .getResponseBody();


        Long id = Objects.requireNonNull(allTasksResponse)
                .stream()
                .map(TaskDtoResponse::getIdTask)
                .reduce((first, second) -> second)
                .orElseThrow();

        UpdateTaskDtoRequest updateTaskDtoRequest = new UpdateTaskDtoRequest();
        updateTaskDtoRequest.setNameTask("newTaskName");
        updateTaskDtoRequest.setDescriptionTask("newDescription");

        webTestClient.patch()
                .uri("/task/" + id)
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .bodyValue(updateTaskDtoRequest)
                .exchange()
                .expectStatus()
                .isOk();

        var updatedTask = webTestClient.get()
                .uri(String.format("/information-task/" + id))
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(TaskDtoResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(updatedTask.getNameTask()).isEqualTo(updateTaskDtoRequest.getNameTask());
        assertThat(updatedTask.getDescriptionTask()).isEqualTo(updateTaskDtoRequest.getDescriptionTask());


    }

    @Test
    void removeTaskFromProject() {


    }

    @Test
    void deleteTask_ExistingId_SuccessfulDelete () {
            WebUser mockUser = WebUser.builder()
                    .idWebUser(1L)
                    .nameUser("uiui")
                    .mailUser("afaf")
                    .passwordUser("123123")
                    .build();

            MyUserDetails userDetails = new MyUserDetails(mockUser.getIdWebUser(), mockUser.getMailUser(), mockUser.getPasswordUser(), null);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            String accessToken = jwtUtils.generateToken(authentication);
            when(customUserDetailsService.loadUserByUsername("afaf")).thenReturn(userDetails);

            String mockBearerToken = "Bearer " + accessToken;

            CreateTaskDtoRequest request =
                    CreateTaskDtoRequest.builder()
                            .nameTask("testIT")
                            .build();
            webTestClient.post()
                    .uri("/task")
                    .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                    .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus()
                    .isCreated();

            var allTasksResponse = webTestClient.get()
                    .uri("/tasks-user")
                    .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBodyList(TaskDtoResponse.class)
                    .returnResult()
                    .getResponseBody();


            Long id = Objects.requireNonNull(allTasksResponse)
                    .stream()
                    .map(TaskDtoResponse::getIdTask)
                    .reduce((first, second) -> second)
                    .orElseThrow();

            webTestClient.delete()
                    .uri("/task/" + id)
                    .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                    .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .exchange()
                    .expectStatus()
                    .isOk();

            var deletedTask = webTestClient.get()
                    .uri(String.format("/information-task/" + id))
                    .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                    .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .exchange()
                    .expectStatus()
                    .isNotFound();


        }
    }
