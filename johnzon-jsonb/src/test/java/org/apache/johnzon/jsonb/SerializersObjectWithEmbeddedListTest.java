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

import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.annotation.JsonbTypeSerializer;
import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SerializersObjectWithEmbeddedListTest
{
    @Test
    public void serializeTest() throws Exception
    {
        ObjectModel objectModel = new ObjectModel();
        objectModel.embeddedList.add("Text1");
        objectModel.embeddedList.add("Text2");
        objectModel.otherField = "Other Text";

        WrapperModel wrapper = new WrapperModel();
        wrapper.object = objectModel;

        try (final Jsonb jsonb = JsonbBuilder.create()) {
            jsonb.toJson(wrapper);
        }
    }

    public static class WrapperModel implements Serializable {
        public ObjectModel object;
    }

    @JsonbTypeSerializer(ObjectDeSer.class)
    public static class ObjectModel implements Serializable
    {
        public List<String> embeddedList = new ArrayList<>();
        public String otherField;
    }

    public static class ObjectDeSer implements JsonbSerializer<ObjectModel> {

        @Override
        public void serialize(ObjectModel obj, JsonGenerator generator, SerializationContext ctx) {
            ctx.serialize("embeddedList", obj.embeddedList, generator); // this closes the whole object
            ctx.serialize("otherField", obj.otherField, generator); // this is outside the object
        }
    }
}
