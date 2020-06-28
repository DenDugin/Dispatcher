package com.dispatcher.dispatcher;


import com.dispatcher.dispatcher.component.Docker;
import com.dispatcher.dispatcher.component.RabbitConfiguration;
import com.dispatcher.dispatcher.component.Sender;
import com.dispatcher.dispatcher.controller.MainController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestPropertySource(locations="classpath:application.properties")
@ContextConfiguration(classes = {MainController.class, Docker.class,Sender.class, RabbitConfiguration.class})
@RunWith(SpringRunner.class)
@WebMvcTest(MainController.class)
public class MainControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RabbitTemplate template;

    @Test
    public void givenEmployees_whenGetEmployees_thenReturnJsonArray() throws Exception {

        mvc.perform(post("/api/dispatcher").contentType(MediaType.APPLICATION_XML_VALUE).content("test"))
                .andExpect(status().isOk());
    }





}
