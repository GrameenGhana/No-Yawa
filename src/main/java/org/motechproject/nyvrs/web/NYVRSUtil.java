/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.motechproject.nyvrs.web;

import org.motechproject.nyvrs.domain.ClientRegistration;
import org.motechproject.nyvrs.web.domain.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author seth
 */
public class NYVRSUtil {

    private static final Logger LOG = LoggerFactory.getLogger(RegistrationController.class);

    public static String getMsgToPay(ClientRegistration client, int day) {
        if (null == client) {
            LOG.info("Client with callerI is NOT registered in NYVRS");
            return "";
        } else {

            if (day == 0) {
                return String.format("Week%dStory%d", client.getNyWeeks(), client.getNyWeeks());
            } else if (day > 0) {
                return (String.format("%sDay%dWeek%02d", client.getCampaignType().getValue(), day, client.getNyWeeks()));
            }
        }
        return "";
    }

    /**
     * default dg di en ew ha ti
     *
     * @param language
     * @return
     */
    public static String processLanguage(String language) {
        if (null == language || language.isEmpty()) {
            language = Language.ENGLISH.toString();
        }

        language = language.toLowerCase();
        //English, Twi, Ewe, Hausa, Dangme, Dagbani, Dagaare, Kassem, Gonja
        if (language.equalsIgnoreCase("english")) {
            return "en";
        } else if (language.equalsIgnoreCase("Dangme")) {
            return "dg";
        } else if (language.equalsIgnoreCase("Dagbani")) {
            return "di";
        } else if (language.equalsIgnoreCase("Twi")) {
            return "ti";
        } else if (null == language) {
            return "en";
        } else if (language.length() < 2) {
            return language;
        } else {
            return language.substring(0, 2);
        }

    }

}
