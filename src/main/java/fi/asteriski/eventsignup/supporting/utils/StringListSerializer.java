package fi.asteriski.eventsignup.supporting.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.List;

public class StringListSerializer extends JsonSerializer<List<String>> {
    @Override
    public void serialize(List<String> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null || value.isEmpty()) {
            gen.writeNull();
        } else if (value.size() == 1) {
            gen.writeString(value.get(0)); // plain string
        } else {
            gen.writeStartArray();
            for (String item : value) {
                gen.writeString(item);
            }
            gen.writeEndArray(); // proper array
        }
    }
}
