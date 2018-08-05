package com.example.testapp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TestappApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	@Before
    public void clean() throws Exception {
	    String list = mockMvc.perform(get("/list")).andReturn().getResponse().getContentAsString();
	    List<String> ids = new ArrayList<>();
	    String[] strings = list.split(",");
        for (String s: strings) {
            int start = s.indexOf("id=");
            if (start > 0)
                ids.add(s.substring(start + "id=".length()));
        }

        for (String id: ids) {
            mockMvc.perform(get("/delete?id=" + id)).andReturn();
        }
    }

	@Test
	public void testCreate() throws Exception {
		mockMvc.perform(
				get("/create?username=aaa&name=bbb&email=a@b.com&password=ccc"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(
				        "\"name\":\"bbb\",\"username\":\"aaa\",\"email\":\"a@b.com\",\"password\":\"ccc\"")));
	}

	@Test
    public void testRead() throws Exception {
        String user = mockMvc.perform(
                get("/create?username=aaa&name=bbb&email=a@b.com&password=ccc"))
                .andReturn().getResponse().getContentAsString();
        String[] strings = user.split(",");
        for (String s: strings) {
            int start = s.indexOf("id=");
            if (start > 0) {
                String id = s.substring(start + "id=".length());
                mockMvc.perform(get("/read?id=" + id))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString(user)));
                return;
            }
        }
    }
}
