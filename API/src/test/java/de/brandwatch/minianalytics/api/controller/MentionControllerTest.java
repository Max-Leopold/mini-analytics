package de.brandwatch.minianalytics.api.controller;

import de.brandwatch.minianalytics.api.service.MentionService;
import de.brandwatch.minianalytics.api.solr.model.Mention;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MentionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MentionService mentionService;

    @Test
    public void testMentionControllerWithputDate() throws Exception {
        Mention mention = new Mention();
        mention.setText("Hello World");
        mention.setQueryID(1);
        mention.setAuthor("Max Leopold");
        mention.setDate(Instant.now());

        List<Mention> mentionList = new ArrayList<>();
        mentionList.add(mention);
        when(mentionService.getMentionsFromQueryID("1", "")).thenReturn(mentionList);

        mockMvc.perform(get("/mentions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author", is("Max Leopold")))
                .andExpect(jsonPath("$[0].queryID", is(1)))
                .andExpect(jsonPath("$[0].text", is("Hello World")))
                .andExpect(jsonPath("$[0].date").exists())
                .andDo(print());
    }

    @Test
    public void testMentionControllerWithDate() throws Exception {
        String date = "2019-01-01";

        Mention mention = new Mention();
        mention.setText("Hello World");
        mention.setQueryID(1);
        mention.setDate(Instant.ofEpochMilli(1546300800)); //01.01.2019 00:00
        mention.setAuthor("Max Leopold");

        List<Mention> mentionList = new ArrayList<>();
        mentionList.add(mention);
        when(mentionService.getMentionsFromQueryID("1", date)).thenReturn(mentionList);

        mockMvc.perform(get("/mentions/1")
            .param("date", date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author", is("Max Leopold")))
                .andExpect(jsonPath("$[0].queryID", is(1)))
                .andExpect(jsonPath("$[0].text", is("Hello World")))
                .andExpect(mvcResult -> {
                    JSONArray jsonArray = new JSONArray(mvcResult.getResponse().getContentAsString());
                    JSONObject jsonObjectDate = jsonArray.getJSONObject(0).getJSONObject("date");
                    Instant dateOfMention = Instant.ofEpochSecond(Long.parseLong(jsonObjectDate.get("seconds").toString() + (Long.parseLong(jsonObjectDate.getString("nanos").toString()) / 1000000)));

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                    String convertedDate = date + " 00:00:00";
                    TemporalAccessor temporalAccessor = formatter.parse(convertedDate);
                    LocalDateTime localDateTime = LocalDateTime.from(temporalAccessor);
                    ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("UTC"));
                    Instant earliestAllowedDate = Instant.from(zonedDateTime);

                    if(dateOfMention.isBefore(earliestAllowedDate)){
                        throw new Exception("Date of mention is before the earliste allowed date");
                    }
                })
                .andDo(print());
    }
}
