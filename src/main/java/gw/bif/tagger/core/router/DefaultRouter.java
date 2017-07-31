package gw.bif.tagger.core.router;

import java.util.Map;

import javax.inject.Inject;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import gw.bif.tagger.core.aggregator.DefaultAggregationStrategy;

@Component
public class DefaultRouter extends RouteBuilder {

	private final ProducerTemplate producerTemplate;

	private final DefaultAggregationStrategy aggregationStrategy;

	private final NamespaceRouter namespaceRouter;

	private final AttributeMapSplitter attributeMapSplitter;

	private NameSpaceAttributeSplitProcessor nameSpaceAttributeProcessor;
	
	private CallbackProcessor callbackProcessor;
	
	private NamespacePropertyProcessor namespacePropertyProcessor;

	@Inject
	public DefaultRouter(ProducerTemplate producerTemplate, DefaultAggregationStrategy aggregationStrategy,
			NamespaceRouter namespaceRouter, AttributeMapSplitter attributeMapSplitter,
			NameSpaceAttributeSplitProcessor nameSpaceAttributeProcessor, CallbackProcessor callbackProcessor,
			NamespacePropertyProcessor namespacePropertyProcessor) {
		this.producerTemplate = producerTemplate;
		this.aggregationStrategy = aggregationStrategy;
		this.namespaceRouter = namespaceRouter;
		this.attributeMapSplitter = attributeMapSplitter;
		this.nameSpaceAttributeProcessor = nameSpaceAttributeProcessor;
		this.callbackProcessor = callbackProcessor;
		this.namespacePropertyProcessor = namespacePropertyProcessor;
	}

	@Override
	public void configure() throws Exception {
		from("direct:in")//
				.onCompletion().process(callbackProcessor).end()//
				.process(nameSpaceAttributeProcessor)//
				.split()//
				.method(attributeMapSplitter, "split")//
				.aggregationStrategy(aggregationStrategy)//
				.parallelProcessing()//
				.process(namespacePropertyProcessor)//
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

	public void route(Map<String, Object> message) {
		producerTemplate.sendBody("direct:in", message);
	}

}
