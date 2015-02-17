/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.motechproject.nyvrs.migration.util;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.motechproject.nyvrs.domain.CampaignType;
import org.motechproject.nyvrs.domain.ClientRegistration;
import org.motechproject.nyvrs.domain.StatusType;
import org.motechproject.nyvrs.service.CampaignService;
import org.motechproject.nyvrs.service.ClientRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author seth
 */
public class CampaignAssignment {

    CampaignService campaignService;
    ClientRegistrationService clientRegistrationService;

    public CampaignAssignment(ClientRegistrationService clientRegistrationService, CampaignService serv) {
        this.clientRegistrationService = clientRegistrationService;
        this.campaignService = serv;
    }

    public void doAssignment() throws InterruptedException {
        List<ClientRegistration> clients = Collections.EMPTY_LIST;
        System.out.println("doAssignment");
        CampaignType[] types = {CampaignType.RONALD};
        String output = "%s Week %d Age %d %s Size %d";
        for (CampaignType campaignType : CampaignType.values()) {
            //Weeks of subscribers
            for (Integer wk = 24; wk <= 24; wk++) {
                //Valid age range 15-24
                for (Integer age = 18; age <= 19; age++) {
                    clients = clientRegistrationService.findByCampaignStatusWeekAge(campaignType, StatusType.Completed, wk, age);
                    int tt = 0;
                    if (age == 18) {
                        tt = clients.size();
                        System.out.println("Client Length :" + tt);
                        if(tt>1928)
                        clients = clients.subList(1928, clients.size());
                    }
                     tt = clients.size();
                    System.out.println(String.format(output, campaignType, wk, age, "", tt));
                    int cnt = 0;
                    for (ClientRegistration clientRegistration : clients) {
                        try {
                            cnt++;
                            System.out.println(String.format(output, campaignType, wk, age, "#" + cnt + " of ", tt));

                            campaignService.enrollToCampaigns(clientRegistration);
                            if (cnt % 500 == 0) {
                                Thread.sleep(1000);
                            }
                        } catch (Exception e) {
                            System.out.println("Exception e :" + e.getLocalizedMessage());
                        }
                    }

//                for (ClientRegistration clientRegistration : clients) {
//                    System.out.println("Counter :" + counter);
//                    try {
//                        clientRegistrationService.enrollForCampaign(clientRegistration);
//                    } catch (Exception e) {
//                        System.out.println("Unablt to for : " + e.getLocalizedMessage());
//                    }
//                    counter++;
//                    if (counter % 100 == 0) {
//                        System.out.println(String.format("Counter for %s 1000ms ", counter));
//                    }
//                    if (counter % 1000 == 0) {
//                        System.out.println(String.format("Sleeping for %s 1000ms ", counter));
//                        Thread.sleep(1000l);
//                    }
//                }
                }
            }
        }
    }

}
