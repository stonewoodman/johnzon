/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.johnzon.jsonb;

import org.junit.Before;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.annotation.JsonbTypeDeserializer;
import javax.json.bind.annotation.JsonbTypeSerializer;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SerializersMapTest
{
    @Before
    public void init() {
        MapDeSer.serializerCalled = false;
        MapDeSer.deserializerCalled = false;
    }

    @Test
    public void serializeMapTest() throws Exception
    {
        MapModel mapModel = new MapModel();
        mapModel.map.put("key1", "value1");
        mapModel.map.put("key2", "value2");

        try (final Jsonb jsonb = JsonbBuilder.create()) {
            jsonb.toJson(mapModel);
        }

        assertTrue(MapDeSer.serializerCalled);
        assertFalse(MapDeSer.deserializerCalled);
    }

    @Test
    public void deserializeMapTest() throws Exception
    {
        MapModel mapModel = new MapModel();
        mapModel.map.put("key1", "value1");
        mapModel.map.put("key2", "value2");

        try (final Jsonb jsonb = JsonbBuilder.create()) {
            jsonb.fromJson("{ \"map\": { \"key1\": \"value1\", \"key2\": \"value2\" } }", MapModel.class);
        }

        assertFalse(MapDeSer.serializerCalled);
        assertTrue(MapDeSer.deserializerCalled);
    }

    public static class MapModel implements Serializable
    {
        @JsonbTypeSerializer(MapDeSer.class)
        @JsonbTypeDeserializer(MapDeSer.class)
        public Map<String, String> map = new HashMap<>();
    }

    public static class MapDeSer<T> implements JsonbSerializer<T>, JsonbDeserializer<T> {

        private static boolean serializerCalled;
        private static boolean deserializerCalled;

        @Override
        public T deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            deserializerCalled = true;
            return ctx.deserialize(rtType, parser);
        }

        @Override
        public void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
            serializerCalled = true;
            ctx.serialize(obj, generator);
        }
    }
}
