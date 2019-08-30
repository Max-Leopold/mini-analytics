package de.brandwatch.minianalytics.twitterpuller.twitter;


import de.brandwatch.minianalytics.twitterpuller.kafka.Producer;
import de.brandwatch.minianalytics.twitterpuller.model.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import org.mockito.junit.MockitoJUnitRunner;
import twitter4j.Status;
import twitter4j.User;

import java.util.Date;


@RunWith(MockitoJUnitRunner.class)
public class TweetFetcherTest {

    @Mock
    Producer producer;

    @Test
    public void testIfResourceObjectIsGeneratedCorrectly() {

        Status status = mock(Status.class, RETURNS_DEEP_STUBS);

        Date date = new Date();

        when(status.getText()).thenReturn("Hello World");
        when(status.getUser().getName()).thenReturn("Max Leopold");
        when(status.getCreatedAt()).thenReturn(date);

        ArgumentCaptor<Resource> argumentCaptor = ArgumentCaptor.forClass(Resource.class);

        TwitterPullerStatusListener twitterPullerStatusListener = new TwitterPullerStatusListener(producer);
        TweetFetcher tweetFetcher = new TweetFetcher(twitterPullerStatusListener);

        twitterPullerStatusListener.onStatus(status);

        verify(producer).send(argumentCaptor.capture());

        assertEquals("Hello World", argumentCaptor.getValue().getText());
        assertEquals("Max Leopold", argumentCaptor.getValue().getAuthor());
        assertEquals(date.toInstant(), argumentCaptor.getValue().getDate());
    }
}
