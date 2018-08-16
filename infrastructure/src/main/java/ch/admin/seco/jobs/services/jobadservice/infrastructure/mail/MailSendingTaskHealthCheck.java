package ch.admin.seco.jobs.services.jobadservice.infrastructure.mail;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

public class MailSendingTaskHealthCheck extends AbstractHealthIndicator {

    private final MailSendingTaskRepository mailSendingTaskRepository;

    private final int mailQueueThreshold;

    MailSendingTaskHealthCheck(MailSendingTaskRepository mailSendingTaskRepository, int mailQueueThreshold) {
        this.mailSendingTaskRepository = mailSendingTaskRepository;
        this.mailQueueThreshold = mailQueueThreshold;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        if (mailSendingTaskRepository.findAll().size() > mailQueueThreshold) {
            builder.down();
        } else {
            builder.up();
        }
    }
}

