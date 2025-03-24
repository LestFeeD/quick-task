package com.quick_task.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.quick_task.dao.*;
import com.quick_task.dto.LoginDtoRequest;
import com.quick_task.dto.create.CreateWebUserDtoRequest;
import com.quick_task.dto.response.WebUserDtoResponse;
import com.quick_task.dto.update.UpdateWebUserDtoRequest;
import com.quick_task.entity.*;
import com.quick_task.exception.BadRequestException;
import com.quick_task.exception.EntityNotFoundException;
import com.quick_task.factory.DaoFactory;
import com.quick_task.factory.WebUserFactory;
import com.quick_task.utils.DBService;
import com.quick_task.utils.JwtUtils;
import com.quick_task.utils.PasswordChecker;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

@Service
public class WebUserServiceImpl implements WebUserService {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(WebUserServiceImpl.class);

    private  final WebUserFactory webUserFactory;
    private final PasswordChecker passwordChecker;
    private final EmailService emailSender;
    AuthenticationManager authManager ;
    private final JwtUtils jwtUtils;
    private  final EmailServiceImpl emailService;
    private final AuthenticationService authenticationService;
    private final Executor taskExecutor;

    @Autowired
    public WebUserServiceImpl(WebUserFactory webUserFactory, PasswordChecker passwordChecker, EmailService emailSender, JwtUtils jwtUtils, AuthenticationManager authManager, EmailServiceImpl emailService, AuthenticationService authenticationService, Executor taskExecutor) {
        this.webUserFactory = webUserFactory;
        this.passwordChecker = passwordChecker;
        this.emailSender = emailSender;
        this.jwtUtils = jwtUtils;
        this.authManager = authManager;
        this.emailService = emailService;
        this.authenticationService = authenticationService;
        this.taskExecutor = taskExecutor;
    }


    @Async
    @Override
    public CompletableFuture<String> registerUser(CreateWebUserDtoRequest dtoRequest) {
        return CompletableFuture.supplyAsync(() -> {

            Transaction transaction = DBService.getTransaction();
        try{
            WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
            assert webUserDAO != null;
            if(webUserDAO.findByEmail(dtoRequest.getMailUser()) > 0) {
                throw new BadRequestException("The user with this email already exists.");
            } else {
                String bcryptPassword =  BCrypt.withDefaults().hashToString(12, dtoRequest.getPassword().toCharArray());
                WebUser webUser = WebUser.builder()
                        .nameUser(dtoRequest.getNameUser())
                        .mailUser(dtoRequest.getMailUser())
                        .passwordUser(bcryptPassword)
                        .activated(0)
                        .build();
                webUserDAO.create(webUser);
                transaction.commit();
                ConfirmationToken confirmationToken = createConfirmationTokenRegister(webUser).join();
                logger.info("ConfirmToken ID: {}", confirmationToken.getIdConfirmationToken());
                sendConfirmationEmail(dtoRequest, confirmationToken.getUserToken());
                logger.debug("Successful sending of email confirmation");
                return "Confirmation email sent to " + dtoRequest.getMailUser() + ". Please confirm your account.";
            }
        } catch (SQLException | BadRequestException e) {
            throw new RuntimeException(e);
        }
        }, taskExecutor);

    }

    @Async
    @Override
    public CompletableFuture<String> loginUser(LoginDtoRequest login) throws SQLException {

        return CompletableFuture.supplyAsync(() -> {
            Transaction transaction = DBService.getTransaction();

            try {
                WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
                assert webUserDAO != null;
                WebUser webUser =  webUserDAO.findUserByEmail(login.getEmail());
                logger.info("Found email user: {}", webUser.getMailUser());
                transaction.commit();

                Authentication authentication = authManager.authenticate(
                        new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword()));

                if (authentication.isAuthenticated() && webUser.getActivated() == 1) {
                    return jwtUtils.generateToken(authentication);
                } else {
                    return "fail";
                }
            } catch (Exception e) {
                throw new RuntimeException("Login failed", e);
            }
        }, taskExecutor);
    }

    @Async
    @Override
    public CompletableFuture<ResponseEntity<WebUserDtoResponse>> findUser(String email) throws EntityNotFoundException {
        return CompletableFuture.supplyAsync(() -> {
            Transaction transaction = DBService.getTransaction();
            try {
                WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
                assert webUserDAO != null;

                WebUser webUser =  webUserDAO.findUserByEmail(email);

                transaction.commit();
                return ResponseEntity.ok(webUserFactory.makeUserDto(webUser));
            } catch (SQLException e) {
                transaction.rollback();
                throw new RuntimeException(e);
            }
        }, taskExecutor);
    }

    @Async
    @Override
    public CompletableFuture<Void> updateUser( UpdateWebUserDtoRequest dtoRequest) {
        return CompletableFuture.runAsync(() -> {

            Transaction transaction = DBService.getTransaction();
        try{
            WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
            assert webUserDAO != null;
            Long userId = authenticationService.getCurrentUserId();
            WebUser webUser = webUserDAO.findById(userId);
            logger.info("WebUserId: {}", webUser.getIdWebUser());

            if (dtoRequest.getMailUser() != null) {
                if(webUserDAO.findByEmail(dtoRequest.getMailUser()) > 0) {
                    throw new BadRequestException("The user with this email already exists.");
                } else {
                    ConfirmationToken confirmationToken = createConfirmationToken(webUser).join();
                    if (dtoRequest.getNameUser() == null) {
                        dtoRequest.setNameUser(webUser.getNameUser());

                    }
                    webUser.setTemporaryMail(dtoRequest.getMailUser());
                    sendConfirmationUpdateEmail(dtoRequest, confirmationToken.getUserToken());
                    logger.debug("Successful sending of email confirmation in update method");

                }
            }
            if (dtoRequest.getNameUser() != null) {
                webUser.setNameUser(dtoRequest.getNameUser());
            }
            if (dtoRequest.getPassword() != null) {
                String bcryptPassword =  BCrypt.withDefaults().hashToString(12, dtoRequest.getPassword().toCharArray());
                if(passwordChecker.matches(bcryptPassword, webUser.getPasswordUser())) {
                    throw new BadRequestException("The new password cannot be the same as the current password.");
                } else {
                    webUser.setPasswordUser(bcryptPassword);
                }
            }

            webUserDAO.update(webUser);

            transaction.commit();

        } catch (SQLException  | BadRequestException e) {
            transaction.rollback();
            throw new RuntimeException(e);
        }
        }, taskExecutor);
    }

    @Async
    @Override
    public CompletableFuture<Void> deleteUser() {
        return CompletableFuture.runAsync(() -> {
            Transaction transaction = DBService.getTransaction();
            try {
                WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
                assert webUserDAO != null;
                Long userId = authenticationService.getCurrentUserId();
                ProjectParticipantsDAO projectParticipantsDAO = DaoFactory.getDao(ProjectParticipantsDAO.class);
                CommentProjectDAO commentProjectDAO = DaoFactory.getDao(CommentProjectDAO.class);
                CommentTaskDAO commentTaskDAO = DaoFactory.getDao(CommentTaskDAO.class);
                TaskParticipantsDAO taskParticipantsDAO = DaoFactory.getDao(TaskParticipantsDAO.class);
                StatusDAO statusDAO = DaoFactory.getDao(StatusDAO.class);
                PriorityDAO priorityDAO = DaoFactory.getDao(PriorityDAO.class);
                ConfirmationTokenDAO confirmationTokenDAO = DaoFactory.getDao(ConfirmationTokenDAO.class);

                TaskDAO taskDAO = DaoFactory.getDao(TaskDAO.class);
                StatusTaskDAO statusTaskDAO = DaoFactory.getDao(StatusTaskDAO.class);

                ProjectDAO projectDAO = DaoFactory.getDao(ProjectDAO.class);
                StatusProjectDAO statusProjectDAO = DaoFactory.getDao(StatusProjectDAO.class);
                WebUser webUser = webUserDAO.findById(userId);
                List<Task> tasks  = taskDAO.getAllByIdUser(userId);
                List<Project> projects  = projectDAO.getAllByIdUser(userId);

                if (projectParticipantsDAO != null) {
                    projectParticipantsDAO.deleteByUser(userId);
                }

                if (commentProjectDAO != null) {
                    commentProjectDAO.deleteByUser(userId);
                }

                if (commentTaskDAO != null) {
                    commentTaskDAO.deleteByUser(userId);
                }

                if (taskParticipantsDAO != null) {
                    taskParticipantsDAO.deleteByUser(userId);
                }

                if (statusDAO != null) {
                    statusDAO.deleteByUser(userId);
                }

                if (priorityDAO != null) {
                    priorityDAO.deleteByUser(userId);
                }

                if (confirmationTokenDAO != null) {
                    confirmationTokenDAO.deleteByUser(userId);
                }


                if (taskDAO != null) {
                    for (Task task : tasks) {
                        logger.info("Found task for delete: {}", task.getIdTask());

                        StatusTask statusTask = statusTaskDAO.findByTaskId(task.getIdTask());
                        statusTaskDAO.delete(statusTask.getIdStatusTask());
                        taskDAO.deleteById(task.getIdTask());
                    }
                }

                if (projectDAO != null) {
                    for (Project project : projects) {
                        logger.info("Found project for delete: {}", project.getIdProject());

                        StatusProject statusProject = statusProjectDAO.findByProjectId(project.getIdProject());
                        statusProjectDAO.delete(statusProject.getIdStatusProject());
                        projectDAO.removeById(project.getIdProject());
                    }
                }

                logger.info("Found idUser for delete: {}", webUser.getIdWebUser());
                webUserDAO.deleteById(userId);
                transaction.commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, taskExecutor);

    }

    @Async
    private CompletableFuture<ConfirmationToken> createConfirmationToken(WebUser user) throws SQLException {
        return CompletableFuture.supplyAsync(() -> {

            ConfirmationTokenDAO confirmationTokenDAO = DaoFactory.getDao(ConfirmationTokenDAO.class);
        assert confirmationTokenDAO != null;
        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );
        confirmationToken.setWebUser(user);
        user.setConfirmationTokens(new HashSet<>(List.of(confirmationToken)));

            try {
                confirmationTokenDAO.createConfirmationToken(confirmationToken);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return confirmationToken;
        }, taskExecutor);



    }

    @Async
    private CompletableFuture<ConfirmationToken> createConfirmationTokenRegister(WebUser user) throws SQLException {
        return CompletableFuture.supplyAsync(() -> {

            Transaction transaction = DBService.getTransaction();
        try {
            ConfirmationTokenDAO confirmationTokenDAO = DaoFactory.getDao(ConfirmationTokenDAO.class);
            assert confirmationTokenDAO != null;
            String token = UUID.randomUUID().toString();

            ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );
            confirmationToken.setWebUser(user);
            user.setConfirmationTokens(new HashSet<>(List.of(confirmationToken)));

            confirmationTokenDAO.createConfirmationToken(confirmationToken);
            transaction.commit();

            return confirmationToken;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        }, taskExecutor);

    }

    @Async
    private CompletableFuture<Void> sendConfirmationEmail(CreateWebUserDtoRequest dtoRequest, String token) {
        return CompletableFuture.runAsync(() -> {

            String link = "http://localhost:8080/registration/confirm?token=" + token;
        emailSender.send(dtoRequest.getMailUser(), buildEmail(dtoRequest.getNameUser(), link));
        }, taskExecutor);

    }
    @Async
    private CompletableFuture<Void> sendConfirmationUpdateEmail(UpdateWebUserDtoRequest dtoRequest, String token) {
       return CompletableFuture.runAsync(() -> {

            String link = "http://localhost:8080/registration/confirm?token=" + token;
            emailSender.send(dtoRequest.getMailUser(), buildEmail(dtoRequest.getNameUser(), link));
        }, taskExecutor);

    }

    @Scheduled(fixedRate = 900000,  initialDelay = 5000)
    public void removeExpiredUsers() {
        Transaction transaction = DBService.getTransaction();
        try {
            WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
            assert webUserDAO != null;
            ConfirmationTokenDAO confirmationTokenDAO = DaoFactory.getDao(ConfirmationTokenDAO.class);
            assert confirmationTokenDAO != null;

            LocalDateTime now = LocalDateTime.now();
            Timestamp timestamp = Timestamp.valueOf(now);

            Set<Long> expiredTokens = confirmationTokenDAO.findAllExpiredToken(timestamp);

            for (Long tokenId : expiredTokens) {

                Long userId = webUserDAO.findByIdToken(tokenId);
                WebUser webUser = webUserDAO.findById(userId);

                if (userId != null && webUser.getTemporaryMail() != null) {
                    confirmationTokenDAO.deleteById(tokenId);
                    webUserDAO.deleteById(userId);
                }
            }
            transaction.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<String> confirmToken(String token) {
        return CompletableFuture.supplyAsync(() -> {


            Transaction transaction = DBService.getTransaction();
            try {
                ConfirmationTokenDAO confirmationTokenDAO = DaoFactory.getDao(ConfirmationTokenDAO.class);
                assert confirmationTokenDAO != null;
                WebUserDAO webUserDAO = DaoFactory.getDao(WebUserDAO.class);
                assert webUserDAO != null;

                ConfirmationToken confirmationToken = confirmationTokenDAO.findByToken(token);
                logger.info("ConfirmationToken found: ID={}, ExpiresAt={}",
                        confirmationToken.getIdConfirmationToken(), confirmationToken.getExpiresAt());

                if (confirmationToken.getConfirmedAt() != null) {
                    logger.warn("Token already confirmed at: {}", confirmationToken.getConfirmedAt());
                    throw new IllegalStateException("email already confirmed");
                }

                LocalDateTime expiredAt = confirmationToken.getExpiresAt();
                if (expiredAt.isBefore(LocalDateTime.now())) {
                    logger.warn("Token expired at: {}", expiredAt);
                    throw new IllegalStateException("token expired");
                }

                WebUser user = confirmationToken.getWebUser();
                logger.info("Associated WebUser found: ID={}, CurrentEmail={}, TemporaryEmail={}",
                        user.getIdWebUser(), user.getMailUser(), user.getTemporaryMail());

                user.setActivated(1);

                if (user.getTemporaryMail() != null) {
                    logger.info("Updating user email from temporary email: {}", user.getTemporaryMail());
                    user.setMailUser(user.getTemporaryMail());
                    user.setTemporaryMail(null);
                }

                webUserDAO.update(user);

                confirmationToken.setConfirmedAt(LocalDateTime.now());

                transaction.commit();

                return "confirmed";

            } catch (Exception e) {
                transaction.rollback();
                throw new RuntimeException(e);
            }
        }, taskExecutor);
    }



    public String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }


}
