package com.wirebarly.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirebarly.in.web.account.controller.AccountController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers = {
        AccountController.class,
})
public class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected ResultActions postRequest(String endPoint, Long pathValue, Object request) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.post(endPoint, pathValue)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    protected ResultActions postRequest(String endPoint, String pathValue, Object request) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.post(endPoint, pathValue)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    protected ResultActions postRequest(String endPoint, Object request) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.post(endPoint)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }
}
