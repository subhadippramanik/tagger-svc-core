package gw.bif.tagger.core.aggregator;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.stereotype.Component;

@Component
public class DefaultAggregationStrategy implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		if (oldExchange == null) {
			return newExchange;
		}
		Map<String, String> oldMap = oldExchange.getIn().getBody(Map.class);
		Map<String, String> newMap = newExchange.getIn().getBody(Map.class);		
		Map<String, String> aggregateBody = new HashMap<>();
		aggregateBody.putAll(oldMap);
		aggregateBody.putAll(newMap);
		oldExchange.getIn().setBody(aggregateBody);
		return oldExchange;
	}
}
