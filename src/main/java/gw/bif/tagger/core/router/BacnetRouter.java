package gw.bif.tagger.core.router;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.language.SimpleExpression;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BacnetRouter extends RouteBuilder {

	Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	ProducerTemplate producerTemplate;

	@Override
	public void configure() throws Exception {
		from("direct:in")//
				//.setHeader(Exchange.HTTP_METHOD, constant("POST"))
				//.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))//
				.setBody(new SimpleExpression("${body}"))//
				.marshal().json(JsonLibrary.Jackson)//
				.to("http://localhost:8081/bacnet")//
				.onCompletion().aggregate((Exchange oldExchange, Exchange newExchange) -> {

					logger.info(oldExchange.getIn().getBody());
					logger.info(oldExchange.getOut().getBody());
					logger.info(newExchange.getIn().getBody());
					logger.info(newExchange.getOut().getBody());
					oldExchange.getIn().setBody(newExchange.getIn().getBody());
					return oldExchange;

				}).outMessage().completionTimeout(10_000).log("direct:out");

	}

	public Map<String, String> rout(Map<String, String> message) {
		producerTemplate.sendBody("direct:in", message);
		return message;
	}

}
