package gw.bif.tagger.core.router;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.google.common.collect.Iterables;


@Component
public class NamespacePropertyProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, Map<String, String>> spilittedMessage = (Map<String, Map<String, String>>) exchange
				.getIn().getBody(Map.class);
		exchange.getOut().setBody(Iterables.getOnlyElement(spilittedMessage.values()), Map.class);
		exchange.getOut().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		exchange.setProperty("namespace", Iterables.getOnlyElement(spilittedMessage.keySet()));
	}

}
