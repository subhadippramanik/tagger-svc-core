package gw.bif.tagger.core.router;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

import gw.bif.tagger.core.aggregator.DefaultAggregationStrategy;

@Component
public class BacnetRouter extends RouteBuilder {

	private final Log logger = LogFactory.getLog(this.getClass());
	
	private final ImmutableMap<String, String> targets = ImmutableMap.of("bacnet", "http://localhost:8081/bacnet", "modbus", "http://localhost:8082/modbus");

	@Autowired
	ProducerTemplate producerTemplate;

	@Autowired
	DefaultAggregationStrategy aggregationStartegy;

	@Override
	public void configure() throws Exception {

		// from("direct:in")//
		// .onCompletion()//
		// .process(exchange -> logger.info("Final result " +
		// exchange.getIn().getBody(String.class)))//
		// .end()//
		// //.setBody(new SimpleExpression("${body}"))//
		// //.marshal().json(JsonLibrary.Jackson)//
		// .multicast(aggregationStartegy)//
		// .split(simple("${body.tags}"))//
		// .choice()//
		// .when(body().convertToString().contains("bacnet:"))//
		// .convertBodyTo(Tag.class)
		// .marshal().json(JsonLibrary.Jackson)//
		// .to("http://localhost:8081/bacnet")//
		// .to("log:bacnet received ${body}")//
		//// .when(body().convertToString().contains("modbus:"))//
		//// .convertBodyTo(Tag.class)
		//// .marshal().json(JsonLibrary.Jackson)//
		//// .to("http://localhost:8082/modbus")//
		//// .to("log:modbus received ${body}")//
		// ;

		from("direct:mohit").marshal().json(JsonLibrary.Jackson).process(exchange -> {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Map> bodyOutput = new HashMap();
			JsonNode myj = mapper.readTree(exchange.getIn().getBody(String.class));
			
			Iterator<String> fieldNames = myj.fieldNames();

			while (fieldNames.hasNext()) {
				String fieldName = fieldNames.next();
				String namespace = fieldName.split(":")[0];
				Map fieldValues = bodyOutput.get(namespace);
				if (fieldValues == null) {
					fieldValues = new HashMap<>();
				}
				fieldValues.put(fieldName, myj.get(fieldName));
				bodyOutput.put(namespace, fieldValues);
			}

			String namespaces = "";
			for (String key : bodyOutput.keySet()) {
				exchange.setProperty(key, mapper.writeValueAsString(bodyOutput.get(key)));
				namespaces += key + ",";
			}
			namespaces = namespaces.substring(0, namespaces.length() - 1);
			exchange.getOut().setHeader("namespaces", namespaces);
		}).log("${header.namespaces}")//
		.split(header("namespaces").tokenize(","))
			.process(exchange -> {
				exchange.getOut().setHeader("address", targets.get(exchange.getIn().getBody(String.class)));
				exchange.getOut().setBody(exchange.getIn().getBody());
			})
			.setBody(simple("${property.${body}}"))
			.setHeader("content-type",constant("application/json"))
			.recipientList(simple("${header.address}"))
		.end()//
		//.to("http://localhost:8081/${header.target}").end()//
		.end().log("asmaslkjdlaskjdlkasj")
		.end();
	}

	
	
	public Map<String, String> route(Map<String, String> message) {
		// TaggerMessage taggerMessage = new TaggerMessage();
		// final List<Tag> tags = new ArrayList<>();
		// message.entrySet().stream().forEach(entry -> {
		// tags.add(new Tag(entry.getKey(), entry.getValue()));
		// });
		// taggerMessage.setTags(tags);
		producerTemplate.sendBody("direct:mohit", message);
		return message;
	}

}
