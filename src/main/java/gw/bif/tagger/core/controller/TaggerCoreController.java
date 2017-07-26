package gw.bif.tagger.core.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gw.bif.tagger.core.handler.TagHandler;

@RestController
public class TaggerCoreController {
	
	@Autowired TagHandler tagHandler;
	
	@RequestMapping(value = "/tag", method = RequestMethod.POST)
	public Map<String, String> tag(@RequestBody Map<String, String> message) {
		return tagHandler.handle(message);
	}	
	

}
