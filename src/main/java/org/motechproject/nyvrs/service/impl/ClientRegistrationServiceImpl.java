package org.motechproject.nyvrs.service.impl;

import org.motechproject.nyvrs.domain.ChannelType;
import org.motechproject.nyvrs.domain.ClientRegistration;
import org.motechproject.nyvrs.repository.ClientRegistrationDataService;
import org.motechproject.nyvrs.service.CampaignService;
import org.motechproject.nyvrs.service.ClientRegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import org.motechproject.nyvrs.domain.CampaignType;
import org.motechproject.nyvrs.domain.StatusType;

@Service("clientRegistrationService")
public class ClientRegistrationServiceImpl implements ClientRegistrationService {

    @Autowired
    private ClientRegistrationDataService clientRegistrationDataService;

    @Autowired
    private CampaignService campaignService;

    private static final Logger LOG = LoggerFactory.getLogger(ClientRegistrationService.class);

    @Override
    public void add(ClientRegistration clientRegistration) {

        // Check for existence of NYVRS campaigns
        campaignService.handleNyvrsCampaignsInDb();
        ClientRegistration savedClientRegistration = clientRegistrationDataService.create(clientRegistration);
        LOG.info(String.format("Successfully saved client (with callerId=%s) to database",
                savedClientRegistration.getNumber()));

        enrollForCampaign(clientRegistration);
//        campaignService.enrollToNyvrsCampaigns(
//                savedClientRegistration.getId().toString(), savedClientRegistration.getChannel());
//        LOG.info(String.format("Successfully enrolled client (with callerId=%s) for NYVRS campaigns",
//                savedClientRegistration.getNumber()));

    }

    public void enrollForCampaign(CampaignType campaign, ClientRegistration client) {
        campaignService.enrollToCampaigns(client.getId().toString(), campaign, client.getChannel());
    }

    public void enrollForCampaign(ClientRegistration client) {
        campaignService.enrollToCampaigns(client);
    }

    @Override
    public ClientRegistration findClientRegistrationByNumber(String number) {
        return clientRegistrationDataService.findClientRegistrationByNumber(number);
    }

    @Override
    public List<ClientRegistration> getAll() {
        return clientRegistrationDataService.retrieveAll();
    }

    @Override
    public void delete(ClientRegistration clientRegistration) {
        clientRegistrationDataService.delete(clientRegistration);
    }

    @Override
    public void update(ClientRegistration clientRegistration) {
        clientRegistrationDataService.update(clientRegistration);
    }

    @Override
    public ClientRegistration getById(Long clientRegistrationId) {
        return clientRegistrationDataService.findClientRegistrationById(clientRegistrationId);
    }

    @Override
    public List<ClientRegistration> findByCampaignStatus(CampaignType type, StatusType status) {
        return clientRegistrationDataService.findByCampaignStatus(type, status);
    }

    public List<ClientRegistration> findByCampaignStatus(CampaignType type, StatusType status, Integer nyWeeks) {
        return clientRegistrationDataService.findByCampaignStatusWeek(type, status, nyWeeks);
    }

    @Override
    public void unenroll(ClientRegistration client, String campsign) {
        campaignService.unenrollToCampaigns(client, campsign);
    }

    @Override
    public void deleteCampaign(String campaign) {
        campaignService.deleteCampaign(campaign);
    }

    @Override
    public List<ClientRegistration> findByCampaignStatusWeek(CampaignType type, StatusType status, Integer sz) {
        return clientRegistrationDataService.findByCampaignStatusWeek(type, status, sz);
    }

    @Override
    public List<ClientRegistration> findByCampaignStatusWeekAge(CampaignType type, StatusType status, Integer nyWeeks, Integer age) {
        return clientRegistrationDataService.findByCampaignStatusWeekAge(type, status, nyWeeks, String.valueOf(age));
    }
}
