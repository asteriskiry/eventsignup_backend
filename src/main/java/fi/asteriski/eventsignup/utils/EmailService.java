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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@Log4j2
@RequiredArgsConstructor
@Component
public class EmailService {

    @Value("${default.email.sender.address}")
    private String defaultSender;
    @NonNull
    private JavaMailSender javaMailSender;

    /*
    Format:
    Title date (format as locale specific string)

    description
     */
    private static final String MAIL_MESSAGE_EVENT_TEMPLATE = """
        <html>
        This email was automatically sent by the system. Here's your event.
        ------
        <h1>%s %s</h1>
        <p>
        %s
        </p>
        </html>
        """;
    /*
    Format:
    Event name date (format as locale specific string)
    Event's id User's id
    Event's id User's id
     */
    private static final String MAIL_MESSAGE_SIGNUP_SUCCESSFUL_TEMPLATE = """
        <html>
        You have successfully signed up to %s on the %s.
        
        To cancel your participation: <a href=https://ilmot.asteriski.fi/signup/cancel/%s/%s target="_blank">https://ilmot.asteriski.fi/signup/cancel/%s/%s</a>
        </html>
        """;
    private static final String MAIL_MESSAGE_SIGNUP_CANCELLED_TEMPLATE = """
        <html>
        You have successfully cancelled your participation to %s.
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
    private static final String MAIL_SUBJECT_SIGNUP_SUCCESSFUL_TEMPLATE = "Signup for %s successful";
    private static final String MAIL_SUBJECT_SIGNUP_CANCELLED_TEMPLATE = "Participation to %s cancelled successfully";
    private static final String LOG_ERROR_MESSAGE_TEMPLATE = "Error with email. Error was: %s";

    @Async
    @EventListener
    public void onSavedEventSpringEvent(SavedEventSpringEvent savedEventSpringEvent) {
        User loggedInUser = savedEventSpringEvent.getLoggedInUser();
        Event event = savedEventSpringEvent.getEvent();
        try {
            sendEmail(loggedInUser.getEmail(), defaultSender,
                String.format(MAIL_SUBJECT_EVENT_TEMPLATE, event.getName()),
                String.format(MAIL_MESSAGE_EVENT_TEMPLATE, event.getName(), event.getStartDate(), event.getDescription()));
        } catch (MessagingException messagingException) {
            log.error(String.format(LOG_ERROR_MESSAGE_TEMPLATE, messagingException));
        }
    }

    @Async
    @EventListener
    public void onSignupSuccessfulEvent(SignupSuccessfulSpringEvent signupSuccessfulSpringEvent) {
        Participant participant = signupSuccessfulSpringEvent.getParticipant();
        Event event = signupSuccessfulSpringEvent.getEvent();
        ZonedDateTime zonedDateTime = event.getStartDate().atZone(signupSuccessfulSpringEvent.getUserTimeZone());
        String formattedDateTime = zonedDateTime.format(DateTimeFormatter.RFC_1123_DATE_TIME);
        try {
            sendEmail(participant.getEmail(), defaultSender,
                String.format(MAIL_SUBJECT_SIGNUP_SUCCESSFUL_TEMPLATE, event.getName()),
                String.format(MAIL_MESSAGE_SIGNUP_SUCCESSFUL_TEMPLATE, event.getName(), formattedDateTime,
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
                String.format(MAIL_SUBJECT_SIGNUP_CANCELLED_TEMPLATE, event.getName()),
                String.format(MAIL_MESSAGE_SIGNUP_CANCELLED_TEMPLATE, event.getName()));
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
