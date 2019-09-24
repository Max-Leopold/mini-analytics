package de.brandwatch.minianalytics.api.service;

import de.brandwatch.minianalytics.api.solr.model.Mention;
import de.brandwatch.minianalytics.api.solr.repository.MentionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
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

        Instant startDate = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant endDate = Instant.now();

        String dateBounds = "[" + startDate +" TO " + endDate + "]";

        when(mentionRepository.findMentionsAfterDate(1L, dateBounds)).thenReturn(mentionList);

        assertThat(mentionList, is(equalTo(mentionService.getMentionsFromQueryID("1", startDate, endDate))));
        verify(mentionRepository, times(1)).findMentionsAfterDate(1L, dateBounds);

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

        Instant startDate = Instant.ofEpochMilli(1546300800);
        Instant endDate = Instant.now();

        String dateBounds = "[" + startDate+ " TO " + endDate + "]";
        when(mentionRepository.findMentionsAfterDate(1L, dateBounds)).thenReturn(mentionList);

        assertThat(mentionList, is(equalTo(mentionService.getMentionsFromQueryID("1", startDate, endDate))));
        verify(mentionRepository, times(1)).findMentionsAfterDate(1L, dateBounds);
    }
}
