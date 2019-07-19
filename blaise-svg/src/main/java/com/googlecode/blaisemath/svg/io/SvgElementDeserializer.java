package com.googlecode.blaisemath.svg.io;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.googlecode.blaisemath.svg.SvgElement;

import java.io.IOException;

public class SvgElementDeserializer extends StdDeserializer<SvgElement> {

    public SvgElementDeserializer() {
        super(SvgElement.class);
    }

    @Override
    public SvgElement deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return null;
    }

}
