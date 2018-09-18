package ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.dlq;

import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.dlq.DLQChannels.JOB_AD_ACTION_DLQ_CHANNEL;
import static ch.admin.seco.jobs.services.jobadservice.infrastructure.messagebroker.dlq.DLQChannels.JOB_AD_EVENT_DLQ_CHANNEL;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.jobs.services.jobadservice.application.MailSenderData;
import ch.admin.seco.jobs.services.jobadservice.application.MailSenderService;

@Transactional
public class DLQItemService {

    static final String RELEVANT_ID_KEY = "relevantId";

    static final String KAFKA_RECEIVED_TIMESTAMP = "kafka_receivedTimestamp";

    static final String X_EXCEPTION_MESSAGE = "x-exception-message";

    static final String X_EXCEPTION_STACKTRACE = "x-exception-stacktrace";

    static final String X_ORIGINAL_TOPIC = "x-original-topic";

    private final Logger LOG = LoggerFactory.getLogger(DLQItemService.class);

    private final DLQItemRepository dlqItemRepository;

    private final ObjectMapper objectMapper;

    private final MailSenderService mailSenderService;

    private final DLQItemProperties dlqItemProperties;

    public DLQItemService(DLQItemRepository dlqItemRepository, ObjectMapper objectMapper, MailSenderService mailSenderService, DLQItemProperties dlqItemProperties) {
        this.dlqItemRepository = dlqItemRepository;
        this.objectMapper = objectMapper;
        this.mailSenderService = mailSenderService;
        this.dlqItemProperties = dlqItemProperties;
    }

    @StreamListener(target = JOB_AD_EVENT_DLQ_CHANNEL)
    public void handleEventDLQMessage(Message<?> message) {
        doHandle(message, this::extractPartitionKey);
    }

    @StreamListener(target = JOB_AD_ACTION_DLQ_CHANNEL)
    public void handleActionDLQMessage(Message<?> message) {
        doHandle(message, this::extractPartitionKey);
    }

    public long count() {
        return this.dlqItemRepository.count();
    }

    public Page<DLQItem> getItems(Pageable pageable) {
        return this.dlqItemRepository.findAll(pageable);
    }

    public Optional<DLQItem> findById(String id) {
        return this.dlqItemRepository.findById(id);
    }

    public void delete(String id) {
        this.dlqItemRepository.deleteById(id);
    }

    private <T> void doHandle(Message<T> message, Function<Message<T>, String> idMapper) {
        try {
            DLQItem dlqItem = createDlqItem(message, idMapper);
            this.dlqItemRepository.save(dlqItem);
            this.mailSenderService.send(createMailSenderData(message, dlqItem));
        } catch (Exception e) {
            throw new DLQItemProcessingException(message, e);
        }
    }

    private MailSenderData createMailSenderData(Message<?> message, DLQItem dlqItem) {
        final Map<String, Object> mailVariables = new HashMap<>();
        mailVariables.put("dlqItemId", dlqItem.getId());
        mailVariables.put("originalTopic", dlqItem.getOriginalTopic());
        mailVariables.put("exceptionMessage", extractString(message.getHeaders().get(X_EXCEPTION_MESSAGE)));
        mailVariables.put("exceptionStacktrace", extractString(message.getHeaders().get(X_EXCEPTION_STACKTRACE)));
        mailVariables.put("payloadType", message.getPayload().getClass().getSimpleName());
        mailVariables.put("relevantId", dlqItem.getRelevantId());
        return new MailSenderData.Builder()
                .setTo(dlqItemProperties.getReceivers().toArray(new String[0]))
                .setSubject("mail.dlq.notification.subject")
                .setTemplateName("DLQItemNotification")
                .setTemplateVariables(mailVariables)
                .setLocale(Locale.ENGLISH)
                .build();
    }

    private <T> DLQItem createDlqItem(Message<T> message, Function<Message<T>, String> idMapper) throws JsonProcessingException {
        final String header = this.objectMapper.writeValueAsString(extractHeaderAsString(message.getHeaders()));
        final LocalDateTime errorTime = extractLocalDateTime(message.getHeaders().get(KAFKA_RECEIVED_TIMESTAMP));
        final String payload = extractPayload(message.getPayload());
        final String relevantId = idMapper.apply(message);
        final DLQItem dlqItem = new DLQItem(
                errorTime,
                header,
                payload,
                extractString(message.getHeaders().get(X_ORIGINAL_TOPIC)),
                relevantId
        );
        LOG.debug("Error message received: [timestamp: {}, header:{}, payload:{}]", errorTime, header, payload);
        return dlqItem;
    }


    private String extractPartitionKey(Message<?> message) {
        return extractString(message.getHeaders().get(RELEVANT_ID_KEY));
    }

    private Map<String, String> extractHeaderAsString(MessageHeaders headers) {
        Map<String, String> result = new HashMap<>();
        headers.forEach((key, value) -> result.put(key, extractString(value)));
        return result;
    }

    private <T> String extractPayload(T payload) {
        if (payload instanceof byte[]) {
            return extractString(payload);
        } else {
            try {
                return objectMapper.writeValueAsString(payload);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Could serialize as JSON ", e);
            }
        }
    }

    private String extractString(Object value) {
        if (value instanceof byte[]) {
            return new String((byte[]) value);
        } else {
            return Objects.toString(value);
        }
    }

    private LocalDateTime extractLocalDateTime(Object value) {
        if (value != null) {
            Long epochMilli = (Long) value;
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault());
        } else {
            return null;
        }
    }

}
