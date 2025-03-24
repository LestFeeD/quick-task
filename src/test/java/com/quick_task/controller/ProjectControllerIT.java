package com.quick_task.controller;

import com.quick_task.dao.ProjectDAO;
import com.quick_task.dao.WebUserDAO;
import com.quick_task.dto.create.CreateProjectDtoRequest;
import com.quick_task.dto.create.CreateTaskDtoRequest;
import com.quick_task.dto.response.*;
import com.quick_task.dto.update.UpdateProjectDtoRequest;
import com.quick_task.entity.WebUser;
import com.quick_task.factory.DaoFactory;
import com.quick_task.security.security_config.MyUserDetails;
import com.quick_task.service.MyUserDetailsService;
import com.quick_task.service.WebUserServiceImpl;
import com.quick_task.utils.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProjectControllerIT {


    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    @Autowired
    private WebTestClient webTestClient;
    @Mock
    private WebUserDAO webUserDAO;
    @Mock
    private ProjectDAO projectDAO;
    private AutoCloseable closeable;
    @Mock
    private Executor taskExecutor;
    @Mock
    private WebUserServiceImpl authService;

    @Mock
    private MyUserDetailsService customUserDetailsService;

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
        when(DaoFactory.getDao(ProjectDAO.class)).thenReturn(projectDAO);
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
    void getByNameProjects() {
    }

    @Test
    void informationProject() {
    }

    @Test
    void allProjectBoard() {
    }

    @Test
    void changeTask() {
    }

    @Test
    void allProjectUser() {
    }

    @Test
    void createProject() {

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

        CreateProjectDtoRequest request =
                CreateProjectDtoRequest.builder()
                        .nameProject("testIT")
                        .idPriority(1L)
                        .build();

        webTestClient.post()
                .uri("/projects")
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated();

        var allTasksResponse = webTestClient.get()
                .uri("/all-projects")
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(ProjectDtoResponse.class)
                .returnResult()
                .getResponseBody();

        ProjectDtoResponse projectCreated = Objects.requireNonNull(allTasksResponse)
                .stream()
                .filter(project -> project.getNameProject().equals(request.getNameProject()))
                .reduce((first, second) -> second)
                .orElseThrow(() -> new AssertionError("Project was not created"));


        assertThat(projectCreated.getNameProject()).isEqualTo(request.getNameProject());
    }

    @Test
    void updateProject_WithUpdatedNameAndNewDescription_ReturnUpdatedProject() {
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

        CreateProjectDtoRequest request =
                CreateProjectDtoRequest.builder()
                        .nameProject("testIT")
                        .idPriority(1L)
                        .build();

        webTestClient.post()
                .uri("/projects")
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated();

        var allProjectsResponse = webTestClient.get()
                .uri("/all-projects")
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(ProjectDtoResponse.class)
                .returnResult()
                .getResponseBody();

        Long id = Objects.requireNonNull(allProjectsResponse)
                .stream()
                .map(ProjectDtoResponse::getIdProject)
                .reduce((first, second) -> second)
                .orElseThrow();

        UpdateProjectDtoRequest updateProjectDtoRequest = new UpdateProjectDtoRequest();
        updateProjectDtoRequest.setNameProject("newProjectName");
        updateProjectDtoRequest.setDescriptionProject("newDescription");

        webTestClient.patch()
                .uri("/projects/" + id)
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .bodyValue(updateProjectDtoRequest)
                .exchange()
                .expectStatus()
                .isOk();

        var updatedProject = webTestClient.get()
                .uri(String.format("/information-project/" + id))
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(InformationProjectDtoResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(updatedProject.getNameProject()).isEqualTo(updateProjectDtoRequest.getNameProject());
        assertThat(updatedProject.getDescriptionProject()).isEqualTo(updateProjectDtoRequest.getDescriptionProject());


    }

    @Test
    void deleteProject_ExistingId_SuccessfulDelete () {
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

        CreateProjectDtoRequest request =
                CreateProjectDtoRequest.builder()
                        .nameProject("testProjectIT")
                        .idPriority(1L)
                        .build();

        webTestClient.post()
                .uri("/projects")
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated();

        var allProjectResponse = webTestClient.get()
                .uri("/all-projects")
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(ProjectDtoResponse.class)
                .returnResult()
                .getResponseBody();

        Long id = Objects.requireNonNull(allProjectResponse)
                .stream()
                .map(ProjectDtoResponse::getIdProject)
                .reduce((first, second) -> second)
                .orElseThrow();

        webTestClient.delete()
                .uri("/projects/" + id)
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk();

        var deletedProject = webTestClient.get()
                .uri(String.format("/information-project/" + id))
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void deleteProject_VerifyThatProjectDeletedFromTask_SuccessfulDelete () {
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

        CreateTaskDtoRequest requestTak =
                CreateTaskDtoRequest.builder()
                        .nameTask("taskFromDeletedProject")
                        .build();
        webTestClient.post()
                .uri("/task")
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .bodyValue(requestTak)
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


        Long taskId = Objects.requireNonNull(allTasksResponse)
                .stream()
                .map(TaskDtoResponse::getIdTask)
                .reduce((first, second) -> second)
                .orElseThrow();


        CreateProjectDtoRequest request =
                CreateProjectDtoRequest.builder()
                        .nameProject("testProjectIT")
                        .idPriority(1L)
                        .build();

        webTestClient.post()
                .uri("/projects")
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated();

        var allProjectResponse = webTestClient.get()
                .uri("/all-projects")
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(ProjectDtoResponse.class)
                .returnResult()
                .getResponseBody();

        Long id = Objects.requireNonNull(allProjectResponse)
                .stream()
                .map(ProjectDtoResponse::getIdProject)
                .reduce((first, second) -> second)
                .orElseThrow();

        webTestClient.patch()
                .uri("/project/" + id + "/task/" + taskId)
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient.delete()
                .uri("/projects/" + id)
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk();

        var deletedProject = webTestClient.get()
                .uri(String.format("/information-project/" + id))
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isNotFound();

        var findTask = webTestClient.get()
                .uri(String.format("/information-task/" + taskId))
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .exchange()
                .expectBody(TaskDtoResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(findTask.getProjectId()).isEqualTo(null);

    }



        @Test
    void testAllProjectsUser() {
    }

    @Test
    void deleteProject() {
    }
}