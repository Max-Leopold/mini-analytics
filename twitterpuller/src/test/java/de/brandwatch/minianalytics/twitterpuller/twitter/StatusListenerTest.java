package de.brandwatch.minianalytics.twitterpuller.twitter;


import de.brandwatch.minianalytics.twitterpuller.kafka.Producer;
import de.brandwatch.minianalytics.twitterpuller.model.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import twitter4j.Status;
import twitter4j.User;

import java.util.Date;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatusListenerTest {

    @Mock
    private Producer producer;

    @Mock
    private User user;

    @Mock
    Status status;

    @InjectMocks
    private TwitterPullerStatusListener twitterPullerStatusListener;

    @Test
    void testIfResourceObjectIsGeneratedCorrectly() {

        Date date = new Date();

        when(status.getText()).thenReturn("Hello World");
        when(status.getUser()).thenReturn(user);
        when(user.getName()).thenReturn("Max Leopold");
        when(status.getCreatedAt()).thenReturn(date);

        ArgumentCaptor<Resource> argumentCaptor = ArgumentCaptor.forClass(Resource.class);

        twitterPullerStatusListener.onStatus(status);

        Resource expectedResource = new Resource();
        expectedResource.setAuthor("Max Leopold");
        expectedResource.setDate(date.toInstant());
        expectedResource.setText("Hello World");

        verify(producer).send(eq(expectedResource));
    }
}
