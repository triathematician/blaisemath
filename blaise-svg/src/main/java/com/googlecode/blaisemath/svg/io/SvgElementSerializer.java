package com.googlecode.blaisemath.svg.io;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.googlecode.blaisemath.svg.SvgElement;

import java.io.IOException;

public class SvgElementSerializer extends StdSerializer<SvgElement> {

    public SvgElementSerializer() {
        super(SvgElement.class);
    }

    @Override
    public void serialize(SvgElement value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeObject(value);
    }

}
