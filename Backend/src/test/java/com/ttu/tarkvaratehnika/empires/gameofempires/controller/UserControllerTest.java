package com.ttu.tarkvaratehnika.empires.gameofempires.controller;

import com.ttu.tarkvaratehnika.empires.gameofempires.processor.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    public void testLogOutRemovesUserFromListOfLoggedInUsers() {
        try {
            this.mockMvc.perform(get("/auth/signout")).andExpect(status().isOk())
                    .andExpect(content().string(containsString("{\"result\":\"success\"}")));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}