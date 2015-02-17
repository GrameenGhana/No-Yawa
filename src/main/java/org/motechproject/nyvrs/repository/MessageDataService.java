/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.motechproject.nyvrs.repository;

import java.util.List;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.nyvrs.domain.Message;

/**
 *
 * @author seth
 */
public interface MessageDataService extends MotechDataService<Message> {

    @Lookup
    Message findClientRegistrationByNumber(@LookupField(name = "campaignId") String campaignId,
            @LookupField(name = "week") Integer week,
            @LookupField(name = "nyday") String nyday);

    @Lookup
    List<Message> findMessagesByCampaign(@LookupField(name = "campaignId") String campaign);

    @Lookup
    List<Message> findMessagesByCampaignAndWeek(@LookupField(name = "campaignId") String campaign,
            @LookupField(name = "week") Integer week);
}
