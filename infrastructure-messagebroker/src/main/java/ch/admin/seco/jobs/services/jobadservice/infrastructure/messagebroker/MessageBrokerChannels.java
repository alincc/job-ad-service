package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface MessageBrokerChannels {
    String CREATE_FROM_X28_CONDITION = "headers['action']=='CREATE_FROM_X28'";
    String UPDATE_FROM_X28_CONDITION = "headers['action']=='UPDATE_FROM_X28'";
    String APPROVE_CONDITION = "headers['action']=='APPROVE'";
    String REJECT_CONDITION = "headers['action']=='REJECT'";
    String CANCEL_CONDITION = "headers['action']=='CANCEL'";
    String CREATE_FROM_AVAM_CONDITION = "headers['action']=='CREATE_FROM_AVAM'";

    String JOB_AD_EVENT_CHANNEL = "job-ad-event";

    String JOB_AD_INT_ACTION_CHANNEL = "job-ad-int-action";

    String JOB_AD_INT_EVENT_CHANNEL = "job-ad-int-event";

    @Input(JOB_AD_INT_ACTION_CHANNEL)
    SubscribableChannel jobAdIntActionChannel();

    @Output(JOB_AD_INT_EVENT_CHANNEL)
    MessageChannel jobAdIntEventChannel();

    @Output(JOB_AD_EVENT_CHANNEL)
    MessageChannel jobAdEventChannel();
}
