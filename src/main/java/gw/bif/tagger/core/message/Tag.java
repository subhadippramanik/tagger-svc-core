package gw.bif.tagger.core.message;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Tag {
	public String key;
	public String value;

	@JsonCreator
	public Tag(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Tag [key=" + key + ", value=" + value + "]";
	}
	
}
