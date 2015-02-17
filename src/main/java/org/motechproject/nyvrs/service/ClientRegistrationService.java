package org.motechproject.nyvrs.service;

import org.motechproject.nyvrs.domain.ClientRegistration;

import java.util.List;
import org.motechproject.nyvrs.domain.CampaignType;
import org.motechproject.nyvrs.domain.StatusType;

public interface ClientRegistrationService {

    void add(ClientRegistration clientRegistration);

    ClientRegistration findClientRegistrationByNumber(String number);

    List<ClientRegistration> getAll();

    List<ClientRegistration> findByCampaignStatus(CampaignType type, StatusType status);

    List<ClientRegistration> findByCampaignStatusWeek(CampaignType type, StatusType status, Integer nyWeeks);

    List<ClientRegistration> findByCampaignStatusWeekAge(CampaignType type, StatusType status, Integer nyWeeks, Integer age);

    void delete(ClientRegistration clientRegistration);

    void update(ClientRegistration clientRegistration);

    ClientRegistration getById(Long clientRegistrationId);

    public void enrollForCampaign(CampaignType campaign, ClientRegistration client);

    public void enrollForCampaign(ClientRegistration client);

    public void unenroll(ClientRegistration client, String str);

    public void deleteCampaign(String campaign);
}
