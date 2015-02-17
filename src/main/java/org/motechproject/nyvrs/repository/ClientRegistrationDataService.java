package org.motechproject.nyvrs.repository;

import java.util.List;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.nyvrs.domain.CampaignType;
import org.motechproject.nyvrs.domain.ClientRegistration;
import org.motechproject.nyvrs.domain.EducationLevel;
import org.motechproject.nyvrs.domain.StatusType;

public interface ClientRegistrationDataService extends MotechDataService<ClientRegistration> {

    @Lookup
    ClientRegistration findClientRegistrationByNumber(@LookupField(name = "number") String number);

    @Lookup
    List<ClientRegistration> findByCampaignStatus(@LookupField(name = "campaignType") CampaignType ct,
            @LookupField(name = "status") StatusType st);

    @Lookup
    List<ClientRegistration> findByCampaign(@LookupField(name = "campaignType") CampaignType ct);

    List<ClientRegistration> findByEduLevel(@LookupField(name = "educationLevel") EducationLevel ct);

    @Lookup
    List<ClientRegistration> findByCampaignStatusWeek(
            @LookupField(name = "campaignType") CampaignType ct,
            @LookupField(name = "status") StatusType st,
            @LookupField(name = "nyWeeks") Integer nyweeks);

    @Lookup
    List<ClientRegistration> findByCampaignStatusWeekAge(
            @LookupField(name = "campaignType") CampaignType ct,
            @LookupField(name = "status") StatusType st,
            @LookupField(name = "nyWeeks") Integer nyweeks,
            @LookupField(name = "age") String age);

    @Lookup
    ClientRegistration findClientRegistrationById(@LookupField(name = "id") Long id);

}
