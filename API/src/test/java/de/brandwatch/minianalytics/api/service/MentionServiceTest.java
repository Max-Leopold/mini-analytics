package de.brandwatch.minianalytics.api.service;

import de.brandwatch.minianalytics.api.solr.model.Mention;
import de.brandwatch.minianalytics.api.solr.repository.MentionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentionServiceTest {

    @Mock
    private MentionRepository mentionRepository;

    @InjectMocks
    private MentionService mentionService;

    @Test
    public void testGetMentionsFromQueryWithoutDate(){

        Mention mention = new Mention();
        mention.setText("Hello World");
        mention.setQueryID(1);
        mention.setDate(Instant.now());
        mention.setAuthor("Max Leopold");

        List<Mention> mentionList = new ArrayList<>();
        mentionList.add(mention);

        when(mentionRepository.findByQueryID(1L)).thenReturn(mentionList);

        assertThat(mentionList, is(equalTo(mentionService.getMentionsFromQueryID("1", ""))));
        verify(mentionRepository, times(1)).findByQueryID(1L);

    }

    @Test
    public void testGetMentionsWithDate(){
        Mention mention = new Mention();
        mention.setText("Hello World");
        mention.setQueryID(1);
        mention.setDate(Instant.ofEpochMilli(1546300800)); //01.01.2019 00:00
        mention.setAuthor("Max Leopold");

        List<Mention> mentionList = new ArrayList<>();
        mentionList.add(mention);

        String dateBounds = "[2019-09-01T00:00:00Z TO *]";
        when(mentionRepository.findMentionsAfterDate(1L, dateBounds)).thenReturn(mentionList);

        assertThat(mentionList, is(equalTo(mentionService.getMentionsFromQueryID("1", "2019-09-01"))));
        verify(mentionRepository, times(1)).findMentionsAfterDate(1L, dateBounds);
    }
}
