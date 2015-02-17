package org.motechproject.nyvrs.web;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.nyvrs.domain.ClientRegistration;
import org.motechproject.nyvrs.domain.MessageRequest;
import org.motechproject.nyvrs.domain.MessageRequestStatus;
import org.motechproject.nyvrs.domain.SettingsDto;
import org.motechproject.nyvrs.service.ClientRegistrationService;
import org.motechproject.nyvrs.service.MessageRequestService;
import org.motechproject.nyvrs.service.MessageService;
import org.motechproject.nyvrs.service.SchedulerService;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/web-api/message")
public class MessageController {

    private static final Logger LOG = LoggerFactory.getLogger(MessageController.class);

    private SettingsFacade settingsFacade;

    @Autowired
    private ClientRegistrationService clientRegistrationService;

    @Autowired
    private MessageRequestService messageRequestService;

    @Autowired
    SchedulerService schedulerService;

    @Autowired
    public MessageController(final SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
    }

    @RequestMapping(value = "/result", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<String> result(HttpServletRequest request) {
        String callerId = request.getParameter("callerId");
        Boolean isSuccessful = Boolean.valueOf(request.getParameter("isSuccessful"));

        System.out.println(String.format("Request Restful /result?callerId=%s&isSuccessful=%s", callerId, isSuccessful));
        if (StringUtils.isBlank(callerId) || isSuccessful == null) {
            return new ResponseEntity<String>("Missing parameters.", HttpStatus.OK);
        }
        ClientRegistration clientRegistration = clientRegistrationService.findClientRegistrationByNumber(callerId);
        MessageRequest messageRequest = messageRequestService.findActiveMessageRequestByCallerId(callerId);
        if (clientRegistration == null || messageRequest == null) {
            return new ResponseEntity<String>("Could not find message request for the given callerId.", HttpStatus.OK);
        }

        if (isSuccessful) {
            LOG.info(String.format("Call completed (callerId: %s)", callerId));
            messageRequest.setStatus(MessageRequestStatus.COMPLETED);
            messageRequestService.update(messageRequest);
        } else {
            String maxRetries = settingsFacade.getProperty(SettingsDto.ASTERISK_MAX_RETRIES);
            if (messageRequest.getRetryCount() >= Integer.parseInt(maxRetries)) {
                LOG.info(String.format("Call failed, max retries exceeded (callerId: %s)", callerId));
                messageRequest.setStatus(MessageRequestStatus.FAILED);
            } else {
                messageRequest.setRetryCount(messageRequest.getRetryCount() + 1);
            }
            messageRequestService.update(messageRequest);
        }

        new Thread(new Runnable() {
            public void run() {
                schedulerService.handleScheduledRequests();
            }
        }).start();

        return new ResponseEntity<String>("success", HttpStatus.OK);
    }

}
