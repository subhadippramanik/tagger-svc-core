package gw.bif.tagger.core.router;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

import gw.bif.tagger.core.aggregator.DefaultAggregationStrategy;

@Component
public class DefaultRouter extends RouteBuilder {

	private final Log logger = LogFactory.getLog(this.getClass());

	private final ImmutableMap<String, String> targets = ImmutableMap.of("bacnet", "http://localhost:8081/bacnet",
			"modbus", "http://localhost:8082/modbus");

	@Autowired
	ProducerTemplate producerTemplate;

	@Autowired
	DefaultAggregationStrategy aggregationStrategy;

	@Override
	public void configure() throws Exception {
		from("direct:in")//
				.onCompletion()//
				.process(exchange -> logger.info("Final result " + exchange.getIn().getBody(String.class)))//
				.end()//
				.marshal().json(JsonLibrary.Jackson)
				.process(this::processNamespace)//
				.log("${header.namespaces}")//
				.split(header("namespaces").tokenize(","), aggregationStrategy)//
				.process(exchange -> {
					exchange.getOut().setHeader("address", targets.get(exchange.getIn().getBody(String.class)));
					exchange.getOut().setBody(exchange.getIn().getBody());
				})//
				.setBody(simple("${property.${body}}"))//
				.setHeader("content-type", constant("application/json"))//
				.recipientList(simple("${header.address}"));
	}

	private void processNamespace(Exchange exchange) throws IOException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Map<String, String>> bodyOutput = new HashMap<>();
		JsonNode myj = mapper.readTree(exchange.getIn().getBody(String.class));

		Iterator<String> fieldNames = myj.fieldNames();

		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			String namespace = fieldName.split(":")[0];
			Map<String, String> fieldValues = bodyOutput.get(namespace);
			if (fieldValues == null) {
				fieldValues = new HashMap<>();
			}
			fieldValues.put(fieldName, myj.get(fieldName).asText());
			bodyOutput.put(namespace, fieldValues);
		}

		String namespaces = "";
		for (String key : bodyOutput.keySet()) {
			exchange.setProperty(key, mapper.writeValueAsString(bodyOutput.get(key)));
			namespaces += key + ",";
		}
		namespaces = namespaces.substring(0, namespaces.length() - 1);
		exchange.getOut().setHeader("namespaces", namespaces);
	}

	public Map<String, String> route(Map<String, String> message) {
		producerTemplate.sendBody("direct:in", message);
		return message;
	}

}
