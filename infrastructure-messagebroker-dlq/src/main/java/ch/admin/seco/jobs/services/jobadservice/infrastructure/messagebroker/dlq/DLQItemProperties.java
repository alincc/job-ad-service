package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.dlq;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("jobad.dlq")
@Validated
public class DLQItemProperties {

    /**
     * Enable or disable sending mails when a dead-letter-message has been processed
     */
    private boolean mailSendingEnabled = true;

    /**
     * Receivers of the mails for a dead-letter-message
     */
    @NotEmpty
    private List<String> receivers;

    public boolean isMailSendingEnabled() {
        return mailSendingEnabled;
    }

    public void setMailSendingEnabled(boolean mailSendingEnabled) {
        this.mailSendingEnabled = mailSendingEnabled;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }
}
