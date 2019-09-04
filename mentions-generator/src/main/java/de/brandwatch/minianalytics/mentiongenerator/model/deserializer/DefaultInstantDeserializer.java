package de.brandwatch.minianalytics.mentiongenerator.model.deserializer;

import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class DefaultInstantDeserializer extends InstantDeserializer<Instant> {
    public DefaultInstantDeserializer() {
        super(Instant.class, DateTimeFormatter.ISO_OFFSET_DATE_TIME,
                Instant::from,
                a -> Instant.ofEpochMilli(a.value),
                a -> Instant.ofEpochSecond(a.integer, a.fraction),
                null,
                true);
    }
}
