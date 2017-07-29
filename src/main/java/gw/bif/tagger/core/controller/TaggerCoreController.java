package gw.bif.tagger.core.controller;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gw.bif.tagger.core.handler.TagHandler;

@RestController
public class TaggerCoreController {

	@Inject
	private TagHandler tagHandler;

	@RequestMapping(value = "/tag", method = RequestMethod.POST)
	public Map<String, String> tag(@RequestBody Map<String, String> message) {
		return tagHandler.handle(message);
	}

}
