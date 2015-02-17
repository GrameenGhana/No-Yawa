package org.motechproject.nyvrs.service.impl;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import org.joda.time.LocalDate;
import org.motechproject.messagecampaign.contract.CampaignRequest;
import org.motechproject.messagecampaign.loader.CampaignJsonLoader;
import org.motechproject.messagecampaign.service.MessageCampaignService;
import org.motechproject.messagecampaign.userspecified.CampaignRecord;
import org.motechproject.nyvrs.domain.CampaignType;
import org.motechproject.nyvrs.domain.ChannelType;
import org.motechproject.nyvrs.domain.ClientRegistration;
import org.motechproject.nyvrs.service.CampaignService;
import org.motechproject.server.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("campaignService")
public class CampaignServiceImpl implements CampaignService {

    @Autowired
    private MessageCampaignService messageCampaignService;

    @Autowired
    private SettingsFacade settingsFacade;

    private CampaignJsonLoader campaignJsonLoader = new CampaignJsonLoader();

    private static final Logger LOG = LoggerFactory.getLogger(CampaignService.class);

    @Override
    public void handleNyvrsCampaignsInDb() {
        List<CampaignRecord> nyvrsCampaignRecords;
        boolean isSundayPresent = true;
        boolean isWednesdayPresent = true;
        boolean isSaturdayPresent = true;

        if (messageCampaignService.getCampaignRecord(CampaignService.SUNDAY_MESSAGE_CAMPAIGN_NAME) == null) {
            isSundayPresent = false;
        }

        if (messageCampaignService.getCampaignRecord(CampaignService.WEDNESDAY_MESSAGE_CAMPAIGN_NAME) == null) {
            isWednesdayPresent = false;
        }

        if (messageCampaignService.getCampaignRecord(CampaignService.SATURDAY_MESSAGE_CAMPAIGN_NAME) == null) {
            isSaturdayPresent = false;
        }

        if (!isSundayPresent || !isWednesdayPresent || !isSaturdayPresent) {
            InputStream messageCampaignsJson = settingsFacade.getRawConfig(CampaignService.MESSAGE_CAMPAIGNS_FILENAME);
            nyvrsCampaignRecords = campaignJsonLoader.loadCampaigns(messageCampaignsJson);

            if (!isSundayPresent) {
                for (CampaignRecord campaignRecord : nyvrsCampaignRecords) {
                    if (campaignRecord.getName().equals(CampaignService.SUNDAY_MESSAGE_CAMPAIGN_NAME)) {
                        messageCampaignService.saveCampaign(campaignRecord);
                        LOG.info("Added lacking '" + CampaignService.SUNDAY_MESSAGE_CAMPAIGN_NAME + "' "
                                + "message campaign");
                    }
                }
            }

            if (!isWednesdayPresent) {
                for (CampaignRecord campaignRecord : nyvrsCampaignRecords) {
                    if (campaignRecord.getName().equals(CampaignService.WEDNESDAY_MESSAGE_CAMPAIGN_NAME)) {
                        messageCampaignService.saveCampaign(campaignRecord);
                        LOG.info("Added lacking '" + CampaignService.WEDNESDAY_MESSAGE_CAMPAIGN_NAME + "' "
                                + "message campaign");
                    }
                }
            }

            if (!isSaturdayPresent) {
                for (CampaignRecord campaignRecord : nyvrsCampaignRecords) {
                    if (campaignRecord.getName().equals(CampaignService.SATURDAY_MESSAGE_CAMPAIGN_NAME)) {
                        messageCampaignService.saveCampaign(campaignRecord);
                        LOG.info("Added lacking '" + CampaignService.SATURDAY_MESSAGE_CAMPAIGN_NAME + "' "
                                + "message campaign");
                    }
                }
            }
        }
    }

    public void enrollToNyvrsCampaigns(String externalId, ChannelType channelType, CampaignType type) {
        if (channelType.equals(ChannelType.V)) {
            // enroll clients who signed up for voice for both Wednesday and Saturday voice campaigns
            CampaignRequest wednesdayRequest = new CampaignRequest(externalId, WEDNESDAY_MESSAGE_CAMPAIGN_NAME, new LocalDate(), null);
            messageCampaignService.enroll(wednesdayRequest);

            CampaignRequest saturdayRequest = new CampaignRequest(externalId, SATURDAY_MESSAGE_CAMPAIGN_NAME, new LocalDate(), null);
            messageCampaignService.enroll(saturdayRequest);
        } else {

            String campaign = (CampaignType.KIKI.equals(type)) ? KIKI_MESSAGE_CAMPAIGN_NAME : (CampaignType.RONALD.equals(type)) ? RONALD_MESSAGE_CAMPAIGN_NAME : RITA_MESSAGE_CAMPAIGN_NAME;
            CampaignRequest saturdayRequest = new CampaignRequest(externalId, campaign, new LocalDate(), null);
            messageCampaignService.enroll(saturdayRequest);
        }
        // enroll all clients for SMS campaign
        CampaignRequest sundayRequest = new CampaignRequest(externalId, SUNDAY_MESSAGE_CAMPAIGN_NAME, new LocalDate(), null);

        messageCampaignService.enroll(sundayRequest);
    }

    @Override
    public void enrollToNyvrsCampaigns(String externalId, ChannelType channelType) {
        enrollToNyvrsCampaigns(externalId, channelType, null);
    }

    @Override
    public void enrollToCampaigns(String externalId, CampaignType type, ChannelType channelType) {
        enrollToNyvrsCampaigns(externalId, channelType, type);
    }

    @Override
    public void enrollToCampaigns(ClientRegistration client) {
        ChannelType channelType = client.getChannel();
        String externalId = client.getId().toString();
        CampaignType type = client.getCampaignType();
        if (channelType.equals(ChannelType.V)) {
            try {
                System.out.println("Finding Campaign " + new Date());
                String campaign = (CampaignType.KIKI.equals(type)) ? KIKI_IVR_CAMPAIGN_NAME : (CampaignType.RONALD.equals(type)) ? RONALD_IVR_CAMPAIGN_NAME : RITA_IVR_CAMPAIGN_NAME;

                System.out.println("Campaign Name V: " + campaign);
                enrollToCampaigns(externalId, campaign);

            } catch (Exception e) {
            }

        } else {
            try {
                System.out.println("Finding Campaign " + new Date());
                String campaign = (CampaignType.KIKI.equals(type)) ? KIKI_MESSAGE_CAMPAIGN_NAME : (CampaignType.RONALD.equals(type)) ? RONALD_MESSAGE_CAMPAIGN_NAME : RITA_MESSAGE_CAMPAIGN_NAME;
                System.out.println("Campaign Name V : " + campaign);

                enrollToCampaigns(externalId, campaign);

            } catch (Exception e) {
            }
//            System.out.println("Campaign Name V: "+campaign);
        }
        try {
            System.out.println("Finding Campaign " + new Date());
            enrollToCampaigns(externalId, CampaignService.SUNDAY_MESSAGE_CAMPAIGN_NAME);
        } catch (Exception e) {
            System.out.println("Unable to enroll for Sunday Campaign : " + e.getLocalizedMessage());
        }

    }

    public void enrollToCampaigns(String externalId, String campaign) {
        System.out.println("Enrolment started Campaign " + new Date());
        CampaignRequest request = new CampaignRequest(externalId, campaign, new LocalDate(), null);
        messageCampaignService.enroll(request);
        System.out.println("Enrolment Ended Campaign " + new Date());
    }

    @Override
    public void unenrollToCampaigns(ClientRegistration client, String campaign) {
        messageCampaignService.unenroll(client.getId().toString(), campaign);
    }

    @Override
    public void deleteCampaign(String campaign) {
        messageCampaignService.deleteCampaign(campaign);
    }
}
