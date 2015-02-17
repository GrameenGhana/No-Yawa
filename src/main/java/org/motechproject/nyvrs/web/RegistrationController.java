package org.motechproject.nyvrs.web;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.nyvrs.domain.ClientRegistration;
import org.motechproject.nyvrs.service.ClientRegistrationService;
import org.motechproject.nyvrs.web.domain.RegistrationRequest;
import org.motechproject.nyvrs.web.domain.ValidationError;
import org.motechproject.server.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.motechproject.nyvrs.domain.CampaignType;
import org.motechproject.nyvrs.migration.util.CampaignAssignment;
import org.motechproject.nyvrs.service.CampaignService;

@Controller
@RequestMapping("/web-api")
public class RegistrationController {

    @Autowired
    private SettingsFacade settingsFacade;

    @Autowired
    private ClientRegistrationService clientRegistrationService;

    @Autowired
    CampaignService campaignService;
    private static final String OK = "OK";
    private static final Logger LOG = LoggerFactory.getLogger(RegistrationController.class);

    @RequestMapping("/status")
    @ResponseBody
    public String status() {
        return OK;
    }

    @RequestMapping("/manifest")
    @ResponseBody
    public String manifest(HttpServletRequest request) throws IOException {

        String manifestFileName = settingsFacade
                .getProperty("verboice.manifest.file");
        InputStream manifest = settingsFacade.getRawConfig(manifestFileName);
        String manifestBody = IOUtils.toString(manifest);

        
        String callbackBaseUrl = settingsFacade.getProperty("motech.base.url")
                + settingsFacade.getProperty("verboice.callback.path");

        return manifestBody.replace("URL_BASE", callbackBaseUrl);
    }

    @RequestMapping(value = "/isRegistered", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> isRegistered(HttpServletRequest request) throws IOException {
        String callerId = request.getParameter("callerId");
        System.out.println("Request : " + request.getQueryString());

        if (callerId == null) {
            LOG.info("The request doesn't contain callerId parameter");
            System.out.println("The request doesn't contain callerId parameter");
            return new ResponseEntity<>("The request doesn't callerId parameter", HttpStatus.BAD_REQUEST);
        }

        if (clientRegistrationService.findClientRegistrationByNumber(callerId) != null) {
            LOG.info("Client with callerId " + callerId + " is already registered in NYVRS");
            System.out.println("Client with callerId " + callerId + " is already registered in NYVRS");
            return new ResponseEntity<>("Already registered", HttpStatus.OK);
        } else {
            LOG.info("Client with callerId " + callerId + " is NOT registered in NYVRS");
            System.out.println("Client with callerId " + callerId + " is NOT registered in NYVRS");
            return new ResponseEntity<>("Client is not registered", HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/updateny", method = RequestMethod.GET)
    public ResponseEntity<String> nyUpdater(HttpServletRequest request) {
        String callerId = request.getParameter("callerId");

        if (null == callerId) {
            LOG.info("The request doesn't contain callerId parameter");
            return new ResponseEntity<>("The request doesn't callerId parameter", HttpStatus.BAD_REQUEST);
        } else {
            ClientRegistration client = clientRegistrationService.findClientRegistrationByNumber(callerId);
            if (null == client) {
                LOG.info("Client with callerId " + callerId + " is NOT registered in NYVRS");
                return new ResponseEntity<>("Client is not registered", HttpStatus.OK);
            } else {
                client.setNyWeeks(client.getNyWeeks() + 1);
                clientRegistrationService.update(client);
                return new ResponseEntity<>("update successful", HttpStatus.OK);
            }
        }
    }

    @RequestMapping(value = "/getMsgToPlay", method = RequestMethod.GET)
    public ResponseEntity<String> getPlayMsg(HttpServletRequest request) {
        String callerId = request.getParameter("callerId");
        String dayNo = request.getParameter("day");
        if (null == callerId) {
            LOG.info("The request doesn't contain callerId parameter");
            return new ResponseEntity<>("The request doesn't callerId parameter", HttpStatus.BAD_REQUEST);
        } else {
            ClientRegistration client = clientRegistrationService.findClientRegistrationByNumber(callerId);
            if (null == client) {
                LOG.info("Client with callerId " + callerId + " is NOT registered in NYVRS");
                return new ResponseEntity<>("Client is not registered", HttpStatus.OK);
            } else {
                int day = Integer.parseInt(dayNo);
                if (day == 0) {
                    return new ResponseEntity<>(String.format("Week%sStory%s", client.getNyWeeks(), client.getNyWeeks()), HttpStatus.OK);

                } else if (day > 0) {
//                    int set = 3;
//                    if (client.getCampaignType().equals(CampaignType.KIKI)) {
//                        set = 1;
//                    } else if (client.getCampaignType().equals(CampaignType.RONALD)) {
//                        set = 2;
//                    }
                    return new ResponseEntity<>(String.format("%sDay%dWeek%02d", client.getCampaignType().getValue(), day, client.getNyWeeks()), HttpStatus.OK);
                }
            }
        }
        return null;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<String> register(HttpServletRequest request) {
        String callerId = request.getParameter("callerId");
        String language = request.getParameter("language");
        String age = request.getParameter("age");
        String gender = request.getParameter("gender");
        String educationLevel = request.getParameter("educationLevel");
        String channel = request.getParameter("channel");
        String loc = request.getParameter("location");
        String region = request.getParameter("region");
        String source = request.getParameter("source");
        for (Enumeration e = request.getParameterNames();e.hasMoreElements();) {
            String param = (String) e.nextElement();
            System.out.println(param + ": " + request.getParameter(param));

        }
        System.out.println(String.format("Request Recieved /register?callerId=%s&language=%s&age=%s&gender=%s&educaitonalLevel=%s&channel=%s",
                callerId, language, age, gender, educationLevel, channel));
        List<ValidationError> errors = new ArrayList<ValidationError>();
        RegistrationRequest registrationRequest = null;
        try {
            registrationRequest = new RegistrationRequest(callerId, language, age, gender, educationLevel, channel,source,loc,region);
            errors = registrationRequest.validate();
            if (errors.isEmpty() && clientRegistrationService.findClientRegistrationByNumber(
                    registrationRequest.getCallerId().toString()) != null) {
                System.out.println("Already Exist");
                errors.add(new ValidationError("Already registered"));
            }
        } catch (NullPointerException e) {
            System.out.println("Missing Parameters");
            errors.add(new ValidationError("Missing parameters."));
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid parameters. "+e.getLocalizedMessage());
            errors.add(new ValidationError("Invalid parameters."));
        }

        if (registrationRequest != null && errors.isEmpty()) {
            ClientRegistration clientRegistration = new ClientRegistration(registrationRequest.getCallerId().toString(),
                    registrationRequest.getLanguage().toString(), registrationRequest.getGender().getValue(), registrationRequest.getAge().toString(),
                    registrationRequest.getEducationLevel(), registrationRequest.getChannel()
            , registrationRequest.getSource()
                    , registrationRequest.getRegion()
                    , registrationRequest.getLocation()
            );
            clientRegistrationService.add(clientRegistration);
            System.out.println("Register successful");
            return new ResponseEntity<String>("success", HttpStatus.OK);
        } else {
            return new ResponseEntity<String>(StringUtils.join(errors, ", "), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/register", consumes = "application/json", method = RequestMethod.POST)
    public ResponseEntity<String> register(@RequestBody RegistrationRequest registrationRequest) {

        List<ValidationError> errors = new ArrayList<ValidationError>();
        try {
            errors = registrationRequest.validate();
            if (errors.isEmpty() && clientRegistrationService.findClientRegistrationByNumber(
                    registrationRequest.getCallerId().toString()) != null) {
                errors.add(new ValidationError("Already registered"));
            }
        } catch (IllegalArgumentException e) {
            errors.add(new ValidationError("Invalid or missing arguments"));
        }

        if (registrationRequest != null && errors.isEmpty()) {
            ClientRegistration clientRegistration = new ClientRegistration(registrationRequest.getCallerId().toString(),
                    registrationRequest.getLanguage().getValue(), registrationRequest.getGender().getValue(), registrationRequest.getAge().toString(),
                    registrationRequest.getEducationLevel(), registrationRequest.getChannel());
            clientRegistrationService.add(clientRegistration);
            return new ResponseEntity<String>("success", HttpStatus.OK);
        } else {
            return new ResponseEntity<String>(StringUtils.join(errors, ", "), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/assign", method = RequestMethod.GET)
    public ResponseEntity<String> assignCampaignent(HttpServletRequest request) {
        System.out.println("Messages a:  Started");
        try {
               new CampaignAssignment(clientRegistrationService, campaignService).doAssignment();
        } catch (Exception e) {
            System.out.println("Assignment Error : " + e.getLocalizedMessage());;
            e.printStackTrace();
        }

        return new ResponseEntity<String>("success", HttpStatus.OK);
    }

    @RequestMapping(value = "/enrolls", method = RequestMethod.GET)
    public ResponseEntity<String> enrollSpecific(HttpServletRequest request) {
        System.out.println("Enrolments");
        String campaignName = request.getParameter("campaign");
        String users = request.getParameter("nos");
        String[] userList = users.split(",");

        for (String string : userList) {
            ClientRegistration client = clientRegistrationService.findClientRegistrationByNumber(string);
            if (null == client) {
                System.out.println("Not yet registeredfor NoYawa");

            } else {
                clientRegistrationService.unenroll(client, string);
            }
        }
        try {
//               new CampaignAssignment().doAssignment();
        } catch (Exception e) {
            System.out.println("Assignment Error : " + e.getLocalizedMessage());;
            e.printStackTrace();
        }

        return new ResponseEntity<String>("success", HttpStatus.OK);
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    public ResponseEntity<String> reset(HttpServletRequest request) {
        System.out.println("Reset camapings");

        String[] userList = {"NYVRS SUNDAY IVR CAMPAIGN ITEM", "NYVRS KIKI IVR CAMPAIGN ITEM", "NYVRS RONALD IVR CAMPAIGN ITEM", "NYVRS RITA IVR CAMPAIGN ITEM", "NYVRS RITA SMS CAMPAIGN ITEM", "NYVRS KIKI SMS CAMPAIGN ITEM", "NYVRS RONALD SMS CAMPAIGN ITEM"};

        for (String string : userList) {
            try {
                System.out.println("Resetting  : " + string);
                clientRegistrationService.deleteCampaign(string);
            } catch (Exception e) {
            }
        }
        try {
//               new CampaignAssignment().doAssignment();
        } catch (Exception e) {
            System.out.println("Assignment Error : " + e.getLocalizedMessage());;
            e.printStackTrace();
        }

        return new ResponseEntity<String>("success", HttpStatus.OK);
    }
}
