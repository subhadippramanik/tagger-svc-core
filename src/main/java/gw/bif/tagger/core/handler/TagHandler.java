package gw.bif.tagger.core.handler;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import gw.bif.tagger.core.router.DefaultRouter;

@Component
public class TagHandler {
	
	@Inject
	private DefaultRouter bacnetRotuer;

	public void handle(Map<String, Object> message) {
		bacnetRotuer.route(message);
	}

}
