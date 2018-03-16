package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement;

import ch.admin.seco.jobs.services.jobadservice.application.MailSenderData;
import ch.admin.seco.jobs.services.jobadservice.application.MailSenderService;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisement;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementEvent;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashMap;
import java.util.Map;

import static ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.JobAdvertisementEvents.*;

@Component
public class JobAdvertisementMailEventListener {

    private static Logger LOG = LoggerFactory.getLogger(JobAdvertisementMailEventListener.class);

    private static final String JOB_ADVERTISEMENT_CREATED_SUBJECT = "mail.jobAd.created.subject";
    private static final String JOB_ADVERTISEMENT_CREATED_TEMPLATE = "JobAdCreatedMail.html";
    private static final String JOB_ADVERTISEMENT_APPROVED_SUBJECT = "mail.jobAd.approved.subject";
    private static final String JOB_ADVERTISEMENT_APPROVED_TEMPLATE = "JobAdApprovedMail.html";
    private static final String JOB_ADVERTISEMENT_REJECTED_SUBJECT = "mail.jobAd.rejected.subject";
    private static final String JOB_ADVERTISEMENT_REJECTED_TEMPLATE = "JobAdRejectedMail.html";
    private static final String JOB_ADVERTISEMENT_CANCELLED_SUBJECT = "mail.jobAd.cancelled.subject";
    private static final String JOB_ADVERTISEMENT_CANCELLED_TEMPLATE = "JobAdCancelledMail.html";

    // TODO To clarify which bcc address should be used with the PO (Taken from MailSenderProperties. Maybe move it in the MailSenderService)
    private static final String BCC = "bcc@example.com";  // stellen-mediamatik@seco.admin.ch

    private final JobAdvertisementRepository jobAdvertisementRepository;
    private final MailSenderService mailSenderService;

    @Autowired
    public JobAdvertisementMailEventListener(JobAdvertisementRepository jobAdvertisementRepository, MailSenderService mailSenderService) {
        this.jobAdvertisementRepository = jobAdvertisementRepository;
        this.mailSenderService = mailSenderService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void onCreated(JobAdvertisementEvent jobAdvertisementEvent) {
        if (!JOB_ADVERTISEMENT_CREATED.getDomainEventType().equals(jobAdvertisementEvent.getDomainEventType())) {
            return;
        }
        LOG.debug("Mail catches event JOB_ADVERTISEMENT_CREATED for JobAdvertisementId: {}", jobAdvertisementEvent.getAggregateId());
        final JobAdvertisement jobAdvertisement = jobAdvertisementRepository.getOne(jobAdvertisementEvent.getAggregateId());
        Map<String, Object> variables = new HashMap<>();
        variables.put("jobAdvertisement", jobAdvertisement);
        mailSenderService.send(
                new MailSenderData.Builder()
                        .setTo(jobAdvertisement.getContact().getEmail())
                        .setBcc(BCC)
                        .setSubject(JOB_ADVERTISEMENT_CREATED_SUBJECT)
                        .setTemplateName(JOB_ADVERTISEMENT_CREATED_TEMPLATE)
                        .setTemplateVariables(variables)
                        .setLocale(jobAdvertisement.getContact().getLanguage())
                        .build()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void onApproved(JobAdvertisementEvent jobAdvertisementEvent) {
        if (!JOB_ADVERTISEMENT_APPROVED.getDomainEventType().equals(jobAdvertisementEvent.getDomainEventType())) {
            return;
        }
        LOG.debug("Mail catches event JOB_ADVERTISEMENT_APPROVED for JobAdvertisementId: {}", jobAdvertisementEvent.getAggregateId());
        final JobAdvertisement jobAdvertisement = jobAdvertisementRepository.getOne(jobAdvertisementEvent.getAggregateId());
        Map<String, Object> variables = new HashMap<>();
        variables.put("jobAdvertisement", jobAdvertisement);
        mailSenderService.send(
                new MailSenderData.Builder()
                        .setTo(jobAdvertisement.getContact().getEmail())
                        .setBcc(BCC)
                        .setSubject(JOB_ADVERTISEMENT_APPROVED_SUBJECT)
                        .setTemplateName(JOB_ADVERTISEMENT_APPROVED_TEMPLATE)
                        .setTemplateVariables(variables)
                        .setLocale(jobAdvertisement.getContact().getLanguage())
                        .build()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void onRejected(JobAdvertisementEvent jobAdvertisementEvent) {
        if (!JOB_ADVERTISEMENT_REJECTED.getDomainEventType().equals(jobAdvertisementEvent.getDomainEventType())) {
            return;
        }
        LOG.debug("Mail catches event JOB_ADVERTISEMENT_REJECTED for JobAdvertisementId: {}", jobAdvertisementEvent.getAggregateId());
        final JobAdvertisement jobAdvertisement = jobAdvertisementRepository.getOne(jobAdvertisementEvent.getAggregateId());
        Map<String, Object> variables = new HashMap<>();
        variables.put("jobAdvertisement", jobAdvertisement);
        mailSenderService.send(
                new MailSenderData.Builder()
                        .setTo(jobAdvertisement.getContact().getEmail())
                        .setBcc(BCC)
                        .setSubject(JOB_ADVERTISEMENT_REJECTED_SUBJECT)
                        .setTemplateName(JOB_ADVERTISEMENT_REJECTED_TEMPLATE)
                        .setTemplateVariables(variables)
                        .setLocale(jobAdvertisement.getContact().getLanguage())
                        .build()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void onCancelled(JobAdvertisementEvent jobAdvertisementEvent) {
        if (!JOB_ADVERTISEMENT_CANCELLED.getDomainEventType().equals(jobAdvertisementEvent.getDomainEventType())) {
            return;
        }
        LOG.debug("Mail catches event JOB_ADVERTISEMENT_CANCELLED for JobAdvertisementId: {}", jobAdvertisementEvent.getAggregateId());
        final JobAdvertisement jobAdvertisement = jobAdvertisementRepository.getOne(jobAdvertisementEvent.getAggregateId());
        Map<String, Object> variables = new HashMap<>();
        variables.put("jobAdvertisement", jobAdvertisement);
        mailSenderService.send(
                new MailSenderData.Builder()
                        .setTo(jobAdvertisement.getContact().getEmail())
                        .setBcc(BCC)
                        .setSubject(JOB_ADVERTISEMENT_CANCELLED_SUBJECT)
                        .setTemplateName(JOB_ADVERTISEMENT_CANCELLED_TEMPLATE)
                        .setTemplateVariables(variables)
                        .setLocale(jobAdvertisement.getContact().getLanguage())
                        .build()
        );
    }

}
