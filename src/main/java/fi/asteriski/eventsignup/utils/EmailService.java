/*
Copyright Juhani Vähä-Mäkilä (juhani@fmail.co.uk) 2022.
Licenced under EUROPEAN UNION PUBLIC LICENCE v. 1.2.
 */
package fi.asteriski.eventsignup.utils;

import fi.asteriski.eventsignup.domain.Event;
import fi.asteriski.eventsignup.domain.Participant;
import fi.asteriski.eventsignup.domain.User;
import fi.asteriski.eventsignup.event.SavedEventSpringEvent;
import fi.asteriski.eventsignup.signup.SignupCancelledSpringEvent;
import fi.asteriski.eventsignup.signup.SignupSuccessfulSpringEvent;
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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;


@Log4j2
@RequiredArgsConstructor
@Component
public class EmailService {

    @Value("${default.email.sender.address}")
    private String defaultSender;
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
    private static final String MAIL_MESSAGE_EVENT_TEMPLATE = """
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
    email.message.body.signup.success.to.cancel Event's id User's id, Event's id User's id
     */
    private static final String MAIL_MESSAGE_SIGNUP_SUCCESSFUL_TEMPLATE = """
        <html>
        %s %s %s.
        <br>
        %s: <a href=https://ilmot.asteriski.fi/signup/cancel/%s/%s target="_blank">https://ilmot.asteriski.fi/signup/cancel/%s/%s</a>
        </html>
        """;
    private static final String MAIL_MESSAGE_SIGNUP_CANCELLED_TEMPLATE = """
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
    Name of event
     */
    private static final String LOG_ERROR_MESSAGE_TEMPLATE = "Error with email. Error was: %s";

    @Async
    @EventListener
    public void onSavedEventSpringEvent(SavedEventSpringEvent savedEventSpringEvent) {
        Authentication loggedInUser = savedEventSpringEvent.getLoggedInUser();
        Event event = savedEventSpringEvent.getEvent();
        RefreshableKeycloakSecurityContext ctx = (RefreshableKeycloakSecurityContext) loggedInUser.getCredentials();
        String email = ctx.getToken().getEmail();
        try {
            sendEmail(email, defaultSender,
                String.format(MAIL_SUBJECT_EVENT_TEMPLATE, event.getName()),
                String.format(MAIL_MESSAGE_EVENT_TEMPLATE, messageSource.getMessage("email.message.body.event", null, savedEventSpringEvent.getUsersLocale()), event.getName(), event.getStartDate(), event.getDescription()));
        } catch (MessagingException messagingException) {
            log.error(String.format(LOG_ERROR_MESSAGE_TEMPLATE, messagingException));
        }
    }

    @Async
    @EventListener
    public void onSignupSuccessfulEvent(SignupSuccessfulSpringEvent signupSuccessfulSpringEvent) {
        Participant participant = signupSuccessfulSpringEvent.getParticipant();
        Event event = signupSuccessfulSpringEvent.getEvent();
        String formattedDateTime = event.getStartDate().format(DateTimeFormatter.RFC_1123_DATE_TIME);
        try {
            sendEmail(participant.getEmail(), defaultSender,
                String.format(messageSource.getMessage("email.message.subject.signup.success", null, signupSuccessfulSpringEvent.getUserLocale()),
                    event.getName()),
                String.format(MAIL_MESSAGE_SIGNUP_SUCCESSFUL_TEMPLATE,
                    messageSource.getMessage("email.message.body.signup.success", null, signupSuccessfulSpringEvent.getUserLocale()),
                    event.getName(), formattedDateTime,
                    messageSource.getMessage("email.message.body.signup.success.to.cancel", null, signupSuccessfulSpringEvent.getUserLocale()),
                    event.getId(), participant.getId(),
                    event.getId(), participant.getId()));
        } catch (MessagingException messagingException) {
            log.error(String.format(LOG_ERROR_MESSAGE_TEMPLATE, messagingException));
        }
    }

    @Async
    @EventListener
    public void onSignupCancelledEvent(SignupCancelledSpringEvent signupCancelledSpringEvent) {
        Participant participant = signupCancelledSpringEvent.getParticipant();
        Event event = signupCancelledSpringEvent.getEvent();
        try {
            sendEmail(participant.getEmail(), defaultSender,
                String.format(messageSource.getMessage("email.message.subject.signup.cancelled", null, signupCancelledSpringEvent.getUsersLocale()),
                    event.getName()),
                String.format(MAIL_MESSAGE_SIGNUP_CANCELLED_TEMPLATE,
                    messageSource.getMessage("email.message.body.signup.cancelled", null, signupCancelledSpringEvent.getUsersLocale()),
                    event.getName()));
        } catch (MessagingException messagingException) {
            log.error(String.format(LOG_ERROR_MESSAGE_TEMPLATE, messagingException));
        }
    }

    private void sendEmail(String recipient, String sender, String messageSubject, String messageText) throws MessagingException {
        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg);
        helper.setTo(recipient);
        helper.setFrom(sender);

        helper.setSubject(messageSubject);
        helper.setText(messageText, true);

        try {
            javaMailSender.send(msg);
        } catch (MailAuthenticationException mailAuthenticationException) {
            log.error(String.format("Error authenticating to smtp server. Error was: %s.", mailAuthenticationException));
        } catch (MailSendException mailSendException) {
            log.error(String.format("Error sending email to '%s'. Error was: %s.", recipient, mailSendException));
        } catch (MailException mailException) {
            log.error(String.format(LOG_ERROR_MESSAGE_TEMPLATE, mailException));
        }
    }
}
