package com.googlecode.blaisemath.svg.io;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.AttributeSetCoder;

import java.io.IOException;

/**
 * Serializer for attribute sets in JSON.
 * @see AttributeSetCoder
 * @author Elisha Peterson
 */
public class AttributeSetSerializer extends JsonSerializer<AttributeSet> {

    @Override
    public void serialize(AttributeSet value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(new AttributeSetCoder().encode(value));
    }

}
