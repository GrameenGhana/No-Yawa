/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.motechproject.nyvrs.migration.util;

import java.util.List;
import java.util.concurrent.Callable;
import org.motechproject.nyvrs.domain.ClientRegistration;
import org.motechproject.nyvrs.service.ClientRegistrationService;

/**
 *
 * @author seth
 */
public class CallableDataMigration implements Callable<Boolean> {

    ClientRegistrationService clientRegistrationService;
    List<ClientRegistration> clients;
    int callableIndex;
    String details="";

    public CallableDataMigration(int callableIndex, ClientRegistrationService clientRegistrationService, List<ClientRegistration> clients) {
        this.clientRegistrationService = clientRegistrationService;
        this.clients = clients;
        this.callableIndex = callableIndex;
    }
    public CallableDataMigration(int callableIndex,String details, ClientRegistrationService clientRegistrationService, List<ClientRegistration> clients) {
        this.clientRegistrationService = clientRegistrationService;
        this.clients = clients;
        this.callableIndex = callableIndex;
        this.details = details;
    }

    @Override
    public Boolean call() throws Exception {
        int counter = 0;

        for (ClientRegistration clientRegistration : clients) {
            counter++;
            try {
                System.out.println(String.format("%s - Enrolment batch #%d No #%d", details,callableIndex, counter));
                clientRegistrationService.enrollForCampaign(clientRegistration);
                System.out.println("Enrollment successfull");
            } catch (Exception e) {
                System.out.println("Unable to enrol :" + e.getLocalizedMessage());
            }
        }

        return false;
    }

}
