/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.utils;

import fi.asteriski.eventsignup.event.SavedEventSpringEvent;
import fi.asteriski.eventsignup.event.SignupCancelledSpringEvent;
import fi.asteriski.eventsignup.event.SignupSuccessfulSpringEvent;
import jakarta.mail.MessagingException;
import java.time.format.DateTimeFormatter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Component
public class EmailService {

    @Value("${fi.asteriski.config.email.default-sender-address}")
    private String defaultSender;

    @Value("${fi.asteriski.config.email.base-url}")
    private String baseUrl;

    @NonNull
    private JavaMailSender javaMailSender;

    @NonNull
    private MessageSource messageSource;

    /*
    Format:
    email.message.body.event
    Title date (format as locale specific string)

    description
     */
    private static final String MAIL_MESSAGE_EVENT_TEMPLATE =
            """
        <html>
        %s
        <br>
        ------
        <h1>%s %s</h1>
        <p>
        %s
        </p>
        </html>
        """;
    /*
    Format:
    email.message.body.signup.success
    Event name date (format as locale specific string)
    email.message.body.signup.success.to.cancel Base url Event's id User's id, Base url Event's id User's id
     */
    private static final String MAIL_MESSAGE_SIGNUP_SUCCESSFUL_TEMPLATE =
            """
        <html>
        %s %s %s.
        <br>
        %s: <a href=%s/signup/cancel/%s/%s target="_blank">%s/signup/cancel/%s/%s</a>
        </html>
        """;
    /*
    Format:
    email.message.body.signup.cancelled
    Name of the event
     */
    private static final String MAIL_MESSAGE_SIGNUP_CANCELLED_TEMPLATE =
            """
        <html>
        %s %s.
        </html>
        """;
    /*
    Format:
    Name of event
     */
    private static final String MAIL_SUBJECT_EVENT_TEMPLATE = "[Event signup] %s";
    /*
    Format:
    Thrown exception
     */
    private static final String LOG_ERROR_MESSAGE_TEMPLATE = "Error with email. Error was: %s";

    @Async
    @EventListener
    public void onSavedEventSpringEvent(SavedEventSpringEvent savedEventSpringEvent) {
        var loggedInUser = savedEventSpringEvent.getLoggedInUser();
        var eventDto = savedEventSpringEvent.getEventDto();
        var ctx = (RefreshableKeycloakSecurityContext) loggedInUser.getCredentials();
        var email = ctx.getToken().getEmail();
        try {
            sendEmail(
                    email,
                    defaultSender,
                    String.format(MAIL_SUBJECT_EVENT_TEMPLATE, eventDto.getName()),
                    String.format(
                            MAIL_MESSAGE_EVENT_TEMPLATE,
                            messageSource.getMessage(
                                    "email.message.body.event", null, savedEventSpringEvent.getUsersLocale()),
                            eventDto.getName(),
                            eventDto.getStartDate(),
                            eventDto.getDescription()));
        } catch (MessagingException messagingException) {
            log.error(String.format(LOG_ERROR_MESSAGE_TEMPLATE, messagingException));
        }
    }

    @Async
    @EventListener
    public void onSignupSuccessfulEvent(SignupSuccessfulSpringEvent signupSuccessfulSpringEvent) {
        var participantEntity = signupSuccessfulSpringEvent.getParticipantDto();
        var eventDto = signupSuccessfulSpringEvent.getEventDto();
        var formattedDateTime = eventDto.getStartDate().format(DateTimeFormatter.RFC_1123_DATE_TIME);
        try {
            sendEmail(
                    participantEntity.getEmail(),
                    defaultSender,
                    String.format(
                            messageSource.getMessage(
                                    "email.message.subject.signup.success",
                                    null,
                                    signupSuccessfulSpringEvent.getUserLocale()),
                            eventDto.getName()),
                    String.format(
                            MAIL_MESSAGE_SIGNUP_SUCCESSFUL_TEMPLATE,
                            messageSource.getMessage(
                                    "email.message.body.signup.success",
                                    null,
                                    signupSuccessfulSpringEvent.getUserLocale()),
                            eventDto.getName(),
                            formattedDateTime,
                            messageSource.getMessage(
                                    "email.message.body.signup.success.to.cancel",
                                    null,
                                    signupSuccessfulSpringEvent.getUserLocale()),
                            baseUrl,
                            eventDto.getId(),
                            participantEntity.getId(),
                            baseUrl,
                            eventDto.getId(),
                            participantEntity.getId()));
        } catch (MessagingException messagingException) {
            log.error(String.format(LOG_ERROR_MESSAGE_TEMPLATE, messagingException));
        }
    }

    @Async
    @EventListener
    public void onSignupCancelledEvent(SignupCancelledSpringEvent signupCancelledSpringEvent) {
        var participantEntity = signupCancelledSpringEvent.getParticipantDto();
        var eventDto = signupCancelledSpringEvent.getEventDto();
        try {
            sendEmail(
                    participantEntity.getEmail(),
                    defaultSender,
                    String.format(
                            messageSource.getMessage(
                                    "email.message.subject.signup.cancelled",
                                    null,
                                    signupCancelledSpringEvent.getUsersLocale()),
                            eventDto.getName()),
                    String.format(
                            MAIL_MESSAGE_SIGNUP_CANCELLED_TEMPLATE,
                            messageSource.getMessage(
                                    "email.message.body.signup.cancelled",
                                    null,
                                    signupCancelledSpringEvent.getUsersLocale()),
                            eventDto.getName()));
        } catch (MessagingException messagingException) {
            log.error(String.format(LOG_ERROR_MESSAGE_TEMPLATE, messagingException));
        }
    }

    private void sendEmail(String recipient, String sender, String messageSubject, String messageText)
            throws MessagingException {
        var msg = javaMailSender.createMimeMessage();
        var helper = new MimeMessageHelper(msg);
        helper.setTo(recipient);
        helper.setFrom(sender);

        helper.setSubject(messageSubject);
        helper.setText(messageText, true);

        try {
            javaMailSender.send(msg);
        } catch (MailAuthenticationException mailAuthenticationException) {
            log.error(
                    String.format("Error authenticating to smtp server. Error was: %s.", mailAuthenticationException));
        } catch (MailSendException mailSendException) {
            log.error(String.format("Error sending email to '%s'. Error was: %s.", recipient, mailSendException));
        } catch (MailException mailException) {
            log.error(String.format(LOG_ERROR_MESSAGE_TEMPLATE, mailException));
        }
    }
}
