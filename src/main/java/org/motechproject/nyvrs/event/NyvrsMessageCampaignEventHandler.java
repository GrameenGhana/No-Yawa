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
import org.motechproject.nyvrs.web.NYVRSUtil;

@Service
public class NyvrsMessageCampaignEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NyvrsMessageCampaignEventHandler.class);
    private MessageService messageService;
    private ClientRegistrationService clientRegistrationService;
    private MessageRequestService messageRequestService;

    @Autowired
    public NyvrsMessageCampaignEventHandler(MessageService messageService, ClientRegistrationService clientRegistrationService,
            MessageRequestService messageRequestService) {
        this.messageService = messageService;
        this.clientRegistrationService = clientRegistrationService;
        this.messageRequestService = messageRequestService;
    }

    @MotechListener(subjects = {EventKeys.SEND_MESSAGE})
    public void handleSendMessage(MotechEvent event) {

        String campaignName = event.getParameters().get("CampaignName").toString();

        System.out.println(String.format("Handling SEND_MESSAGE event {}: message={} from campaign={} for externalId={}", event.getSubject(),
                event.getParameters().get("MessageKey"), campaignName, event.getParameters().get("ExternalID")));

        if (campaignName.startsWith("NYVRS") && campaignName.contains("IVR") && !campaignName.endsWith("ITEM")) {

            System.out.println("Considers By application : "+campaignName);
//            LOG.info("Handling SEND_MESSAGE event {}: message={} from campaign={} for externalId={}", event.getSubject(),
//                    event.getParameters().get("MessageKey"), campaignName, event.getParameters().get("ExternalID"));
            Map<String, Object> parametersMap = event.getParameters();
            String clientId = (String) parametersMap.get("ExternalID");
            String msgKey = (String) event.getParameters().get(EventKeys.MESSAGE_KEY);

            ClientRegistration clientRegistration = clientRegistrationService.getById(Long.valueOf(clientId));
            if (null != clientRegistration) {
                if (clientRegistration.getNyWeeks() >= 26) {
                    LOG.warn("nyweeks greater " + clientRegistration.getNyWeeks());
                    clientRegistrationService.unenroll(clientRegistration, campaignName);
                    return;
                }
                int day = (msgKey.contains("SUNDAY")) ? 0 : (msgKey.contains("SATURDAY")) ? 2 : (msgKey.contains("WEDNESDAY")) ? 1 : 3;

//            if (campaignName.equals(CampaignService.SUNDAY_MESSAGE_CAMPAIGN_NAME)) {
//                clientRegistration.setNyWeeks(clientRegistration.getNyWeeks() + 1);
//                clientRegistrationService.update(clientRegistration);
//            } else {
//
//            }
                MessageRequest messageRequest = new MessageRequest(clientRegistration.getNumber(), clientRegistration.getNyWeeks(),
                        day);
                messageRequest.setMsgFileName(NYVRSUtil.getMsgToPay(clientRegistration, day));
                messageRequestService.add(messageRequest);
                messageService.playMessage(messageRequest);
            } else {
                LOG.info("Client Not Found");
            }
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
