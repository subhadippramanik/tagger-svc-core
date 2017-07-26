package gw.bif.tagger.core.message;

import java.util.List;

public class TaggerMessage {
	public List<Tag> tags;

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

}
