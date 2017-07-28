package gw.bif.tagger.core.router;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.Expression;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.language.SimpleExpression;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gw.bif.tagger.core.aggregator.DefaultAggregationStrategy;
import gw.bif.tagger.core.message.Tag;
import gw.bif.tagger.core.message.TaggerMessage;

@Component
public class BacnetRouter extends RouteBuilder {

	Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	ProducerTemplate producerTemplate;

	@Autowired
	DefaultAggregationStrategy aggregationStartegy;

	@Override
	public void configure() throws Exception {
		from("direct:in")//
				.onCompletion()//
				.process(exchange -> logger.info("Final result " + exchange.getIn().getBody(String.class)))//
				.end()//
				//.setBody(new SimpleExpression("${body}"))//
				//.marshal().json(JsonLibrary.Jackson)//
				.multicast(aggregationStartegy)//
//				.process(exchange -> {
//					
//				})
				.split(simple("${body.tags}"))//
//				.choice()//
//				.when(body().contains("bacnet"))//
//				.to("log:bacnet received ${body.key}")//
//				.when(body().contains("modbus"))//
//				.to("log:modbus received ${body.key}")//
				.to("log:${body.key}")//
				//.choice()//
				//.when().
				//.to("http://localhost:8081/bacnet", "http://localhost:8082/modbus")//
		;

	}

	public Map<String, String> rout(Map<String, String> message) {
		TaggerMessage taggerMessage = new TaggerMessage();
		final List<Tag> tags = new ArrayList<>();
		message.entrySet().stream().forEach(entry -> {
			tags.add(new Tag(entry.getKey(), entry.getValue()));
		});
		taggerMessage.setTags(tags);
		producerTemplate.sendBody("direct:in", taggerMessage);
		return message;
	}
	

}
