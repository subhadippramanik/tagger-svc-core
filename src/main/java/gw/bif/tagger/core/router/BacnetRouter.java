package gw.bif.tagger.core.router;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.language.SimpleExpression;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BacnetRouter extends RouteBuilder {

	Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	ProducerTemplate producerTemplate;

	@Autowired
	AggregationStrategy aggregationStartegy;

	@Override
	public void configure() throws Exception {
		from("direct:in")//
				.onCompletion()//
				.process(exchange -> logger.info(exchange.getIn().getBody(String.class)))//
				.end()//
				.setBody(new SimpleExpression("${body}"))//
				.marshal().json(JsonLibrary.Jackson)//
				.multicast(aggregationStartegy)//
				//.convertBodyTo(type)
				.to("http://localhost:8081/bacnet", "http://localhost:8081/modbus")//
		;

	}

	public Map<String, String> rout(Map<String, String> message) {
		producerTemplate.sendBody("direct:in", message);
		return message;
	}

}
