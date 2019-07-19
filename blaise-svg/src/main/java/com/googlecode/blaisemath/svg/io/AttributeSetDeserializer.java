package com.googlecode.blaisemath.svg.io;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.collect.ImmutableMap;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.AttributeSetCoder;
import com.googlecode.blaisemath.style.Styles;

import java.awt.*;
import java.io.IOException;

/**
 * Deserializer for attribute sets in JSON. Converts color strings for "fill" and "stroke" automatically.
 * @see AttributeSetCoder
 * @author Elisha Peterson
 */
public class AttributeSetDeserializer extends JsonDeserializer<AttributeSet> {

    @Override
    public AttributeSet deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String s = p.readValueAs(String.class);
        return new AttributeSetCoder(ImmutableMap.of(Styles.FILL, Color.class, Styles.STROKE, Color.class)).decode(s);
    }
}
