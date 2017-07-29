package gw.bif.tagger.core.router;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.google.common.collect.Iterables;

import gw.bif.tagger.core.aggregator.DefaultAggregationStrategy;

@Component
public class DefaultRouter extends RouteBuilder {

	private final ProducerTemplate producerTemplate;

	private final DefaultAggregationStrategy aggregationStrategy;

	private final NamespaceRouter namespaceRouter;

	private final AttributeMapSplitter attributeMapSplitter;

	@Inject
	public DefaultRouter(ProducerTemplate producerTemplate, DefaultAggregationStrategy aggregationStrategy,
			NamespaceRouter namespaceRouter, AttributeMapSplitter attributeMapSplitter) {
		this.producerTemplate = producerTemplate;
		this.aggregationStrategy = aggregationStrategy;
		this.namespaceRouter = namespaceRouter;
		this.attributeMapSplitter = attributeMapSplitter;
	}

	@Override
	public void configure() throws Exception {
		from("direct:in")//
				.process(new Processor() {
					@Override
					public void process(Exchange exchange) throws Exception {
						Map<String, String> rawAttributes = exchange.getIn().getBody(Map.class);
						Map<String, Map<String, String>> namespaceAttributesMap = new HashMap<>();
						rawAttributes.entrySet().stream().forEach((entry) -> {
							String[] namespaceAndProperty = entry.getKey().split(":");
							String namespace = namespaceAndProperty[0];
							Map<String, String> existingMappings = namespaceAttributesMap.getOrDefault(namespace,
									new HashMap<>());
							existingMappings.put(entry.getKey(), entry.getValue());
							namespaceAttributesMap.put(namespace, existingMappings);
						});
						exchange.getIn().setBody(namespaceAttributesMap);
					}
				})//
				.split()//
				.method(attributeMapSplitter, "split")//
				.aggregationStrategy(aggregationStrategy)//
				.parallelProcessing()//
				.process((exchange) -> {
					Map<String, Map<String, String>> spilittedMessage = (Map<String, Map<String, String>>) exchange
							.getIn().getBody(Map.class);
					exchange.getOut().setBody(Iterables.getOnlyElement(spilittedMessage.values()), Map.class);
					exchange.getOut().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
					exchange.setProperty("namespace", Iterables.getOnlyElement(spilittedMessage.keySet()));
				})//
				.marshal()//
				.json(JsonLibrary.Jackson)//
				.dynamicRouter()//
				.method(namespaceRouter, "getRoute")//
				.unmarshal()//
				.json(JsonLibrary.Jackson)//
				.end()//
				.end()//
				.log("Body: ${body}");

	}

	public Map<String, String> route(Map<String, String> message) {
		producerTemplate.sendBody("direct:in", message);
		return message;
	}

}
