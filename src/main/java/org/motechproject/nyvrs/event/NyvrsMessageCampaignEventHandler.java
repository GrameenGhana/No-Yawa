package org.motechproject.nyvrs.event;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.messagecampaign.EventKeys;
import org.motechproject.nyvrs.domain.ClientRegistration;
import org.motechproject.nyvrs.domain.MessageRequest;
import org.motechproject.nyvrs.domain.StatusType;
import org.motechproject.nyvrs.service.CampaignService;
import org.motechproject.nyvrs.service.ClientRegistrationService;
import org.motechproject.nyvrs.service.MessageRequestService;
import org.motechproject.nyvrs.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import org.motechproject.nyvrs.domain.CampaignType;
import org.motechproject.nyvrs.service.SchedulerService;
import org.motechproject.nyvrs.web.NYVRSUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Service
public class NyvrsMessageCampaignEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NyvrsMessageCampaignEventHandler.class);
    private MessageService messageService;
    private ClientRegistrationService clientRegistrationService;
    private MessageRequestService messageRequestService;

    @Autowired
    SchedulerService schedulerService;

    @Autowired
    public NyvrsMessageCampaignEventHandler(MessageService messageService, ClientRegistrationService clientRegistrationService,
            MessageRequestService messageRequestService) {
        this.messageService = messageService;
        this.clientRegistrationService = clientRegistrationService;
        this.messageRequestService = messageRequestService;
    }

    @MotechListener(subjects = {EventKeys.SEND_MESSAGE})
    public void handleSendMessage(MotechEvent event) {
        Map<String, Object> parametersMap = event.getParameters();
        String clientId = (String) parametersMap.get("ExternalID");
        String msgKey = (String) event.getParameters().get(EventKeys.MESSAGE_KEY);

        String campaignName = event.getParameters().get("CampaignName").toString();

        System.out.println(
                String.format("Handling SEND_MESSAGE event %s: message=%s from campaign=%s for externalId=%s", event.getSubject(),
                        event.getParameters().get("MessageKey"), campaignName, event.getParameters().get("ExternalID")));

        if (campaignName.startsWith("NYVRS") && campaignName.contains("IVR") && !campaignName.endsWith("ITEM")) {

            System.out.println("Considers By application : " + campaignName);
//            LOG.info("Handling SEND_MESSAGE event {}: message={} from campaign={} for externalId={}", event.getSubject(),
//                    event.getParameters().get("MessageKey"), campaignName, event.getParameters().get("ExternalID"));

            ClientRegistration clientRegistration = clientRegistrationService.getById(Long.valueOf(clientId));
            if (null != clientRegistration) {
                if (clientRegistration.getNyWeeks() >= 26) {
                    LOG.warn("nyweeks greater " + clientRegistration.getNyWeeks());
                    clientRegistrationService.unenroll(clientRegistration, campaignName);
                    return;
                }
                int day = (msgKey.contains("SUNDAY")) ? 0 : (msgKey.contains("SATURDAY")) ? 2 : (msgKey.contains("WEDNESDAY")) ? 1 : 3;
                if (day == 0 && clientRegistration.getNyWeeks() > 16) {
                    clientRegistrationService.unenroll(clientRegistration, campaignName);
                    return;
                }
//          
                MessageRequest messageRequest = new MessageRequest(clientRegistration.getNumber(), clientRegistration.getNyWeeks(),
                        day);
                messageRequest.setMsgFileName(NYVRSUtil.getMsgToPay(clientRegistration, day));
                messageRequestService.add(messageRequest);
                messageService.playMessage(messageRequest);
            } else {
                LOG.info("Client Not Found");
            }
        } else if (campaignName.equalsIgnoreCase("NYVRS_MESSAGE_START_TRIGGER")) {
            System.out.println("Runnung Runnable");
            new Thread(new Runnable() {
                public void run() {
                    schedulerService.handleScheduledRequests();
                }
            }).start();

//        return new ResponseEntity<String>("success", HttpStatus.OK);
        }
    }

    @MotechListener(subjects = {EventKeys.CAMPAIGN_COMPLETED})
    public void handleCompletedCampaignEvent(MotechEvent event) {
        String campaignName = event.getParameters().get("CampaignName").toString();
        if (campaignName.contains(CampaignType.KIKI.toString()) || campaignName.contains(CampaignType.KIKI.toString()) || campaignName.contains(CampaignType.KIKI.toString()) || campaignName.contains(CampaignService.SUNDAY_MESSAGE_CAMPAIGN_NAME)) {
//        if (event.getParameters().get("CampaignName").equals(CampaignService.SUNDAY_MESSAGE_CAMPAIGN_NAME)) {
            LOG.info("Handling CAMPAIGN_COMPLETED event {}: message={} from campaign={} for externalId={}", event.getSubject(),
                    event.getParameters().get("MessageKey"), event.getParameters().get("CampaignName"), event.getParameters().get("ExternalID"));
            Map<String, Object> parametersMap = event.getParameters();
            String clientId = (String) parametersMap.get("ExternalID");

            ClientRegistration clientRegistration = clientRegistrationService.getById(Long.valueOf(clientId));
            clientRegistration.setStatus(StatusType.Completed);
            clientRegistrationService.update(clientRegistration);
            clientRegistrationService.unenroll(clientRegistration, clientId);
        }
    }
}
