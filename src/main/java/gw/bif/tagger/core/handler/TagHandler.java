package gw.bif.tagger.core.handler;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gw.bif.tagger.core.router.DefaultRouter;

@Component
public class TagHandler {
	
	@Autowired DefaultRouter bacnetRotuer;

	public Map<String, String> handle(Map<String, String> message) {
		return bacnetRotuer.route(message);
	}

}
