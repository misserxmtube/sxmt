package com.sxmt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("file:ui/src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml")
public class AppTest {
    private MockMvc mockMvc;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    protected WebApplicationContext wac;
    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
        this.objectMapper = new ObjectMapper();
    }

    @Test
    public void stationsSimple() throws Exception {
        mockMvc.perform(get("/rest/stations")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void newSongSimple() throws Exception {
        StationSong stationSong = new StationSong();
        stationSong.setStation("187541621");
        nextSongPost(stationSong);
    }

    @Test
    public void nextSongSimple() throws Exception {
        StationSong stationSong = new StationSong();
        stationSong.setStation("187541621");
        stationSong.setSong("1234232");
        nextSongPost(stationSong);
    }

    private void nextSongPost(StationSong stationSong) throws Exception {
        mockMvc.perform(post("/rest/song")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stationSong)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.song").exists())
                .andExpect(jsonPath("$.artist").exists());
    }
}
