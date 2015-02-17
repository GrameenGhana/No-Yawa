package org.motechproject.nyvrs.service;

import org.motechproject.nyvrs.domain.CampaignType;
import org.motechproject.nyvrs.domain.ChannelType;
import org.motechproject.nyvrs.domain.ClientRegistration;

public interface CampaignService {
//
    String WEDNESDAY_MESSAGE_CAMPAIGN_NAME ="NYVRS WEDNESDAY IVR CAMPAIGN";
    String SATURDAY_MESSAGE_CAMPAIGN_NAME = "NYVRS SATURDAY IVR CAMPAIGN";
    String SUNDAY_MESSAGE_CAMPAIGN_NAME =   "NYVRS SUNDAY IVR CAMPAIGN";
    String KIKI_MESSAGE_CAMPAIGN_NAME =     "NYVRS KIKI SMS CAMPAIGN";
    String KIKI_IVR_CAMPAIGN_NAME =         "NYVRS KIKI IVR CAMPAIGN";
    String RONALD_MESSAGE_CAMPAIGN_NAME =   "NYVRS RONALD SMS CAMPAIGN";
    String RONALD_IVR_CAMPAIGN_NAME =       "NYVRS RONALD IVR CAMPAIGN";
    String RITA_MESSAGE_CAMPAIGN_NAME =     "NYVRS RITA SMS CAMPAIGN";
    String RITA_IVR_CAMPAIGN_NAME =         "NYVRS RITA IVR CAMPAIGN";
    String MESSAGE_CAMPAIGNS_FILENAME = "message-campaign.json";

    void handleNyvrsCampaignsInDb();

    void enrollToNyvrsCampaigns(String externalId, ChannelType channelType);

    void enrollToCampaigns(String externalId, CampaignType type, ChannelType channelType);

    void enrollToCampaigns(ClientRegistration client);

    void unenrollToCampaigns(ClientRegistration client, String campaign);

    void deleteCampaign(String campaign);
}
