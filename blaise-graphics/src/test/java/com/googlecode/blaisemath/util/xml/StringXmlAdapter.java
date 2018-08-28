package com.googlecode.blaisemath.util.xml;

/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.googlecode.blaisemath.util.encode.StringDecoder;
import com.googlecode.blaisemath.util.encode.StringEncoder;
import static java.util.Objects.requireNonNull;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Uses {@link StringEncoder} and {@link StringDecoder} to perform XML serialization
 * on objects.
 * 
 * @param <X> type to encode/decode
 * @author Elisha Peterson
 */
public class StringXmlAdapter<X> extends XmlAdapter<String, X> {
    
    private final StringEncoder<X> encoder;
    private final StringDecoder<X> decoder;

    /**
     * Construct instance with a class that both encodes and decodes.
     * @param <Y> coder type
     * @param coder coder
     */
    public <Y extends StringEncoder<X> & StringDecoder<X>> StringXmlAdapter(Y coder) {
        this(coder, coder);
    }
    
    /**
     * Construct instance with given encoder and decoder
     * @param encoder encodes
     * @param decoder decodes
     */
    public StringXmlAdapter(StringEncoder<X> encoder, StringDecoder<X> decoder) {
        this.encoder = requireNonNull(encoder);
        this.decoder = requireNonNull(decoder);
    }

    @Override
    public X unmarshal(String v) throws Exception {
        return decoder.decode(v);
    }

    @Override
    public String marshal(X v) throws Exception {
        return encoder.encode(v);
    }
    
}
