package com.huangwei.extra.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Jackson注解 - 反序列化为String<br>
 * <br>
 * 使用方法：@JsonDeserialize(using = DeserializeToString.class)
 */
public class DeserializeToString extends JsonDeserializer<String> {

	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonToken token = p.currentToken();
		if (JsonToken.VALUE_NULL.equals(token)) {
			return null;
		}
		if (JsonToken.START_OBJECT.equals(token) || JsonToken.START_ARRAY.equals(token)) {
			int tokenId = p.currentTokenId(), depth = 0;
			StringBuilder sb = new StringBuilder();
			sb.append(p.getText());
			while ((token = p.nextValue()) != null) {
				if (p.currentTokenId() == tokenId) {
					depth++;
				} else if (p.currentTokenId() == tokenId + 1) {
					sb.append(p.getText());
					if (depth-- > 0) {
						sb.append(",");
						continue;
					} else {
						break;
					}
				}
				if (JsonToken.START_OBJECT.equals(token) || JsonToken.START_ARRAY.equals(token)) {
					if (p.currentName() != null) {
						sb.append("\"").append(p.currentName()).append("\"").append(":");
					}
					sb.append(p.getText());
				} else if (JsonToken.END_OBJECT.equals(token) || JsonToken.END_ARRAY.equals(token)) {
					sb.append(p.getText()).append(",");
				} else {
					if (p.currentName() != null) {
						sb.append("\"").append(p.currentName()).append("\"").append(":");
					}
					if (JsonToken.VALUE_STRING.equals(token)) {
						sb.append("\"").append(p.getText()).append("\"");
					} else {
						sb.append(p.getText());
					}
					sb.append(",");
				}
			}
			return sb.toString().replace(",]", "]").replace(",}", "}");
		}
		return p.getText();
	}

}
