package gw.bif.tagger.core.aggregator;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultAggregationStrategy implements AggregationStrategy {

	Log logger = LogFactory.getLog(this.getClass());

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		if (oldExchange == null) {
			logger.info("Return new exchange");
			return newExchange;
		}	
//		Map oldMap = oldExchange.getIn().getBody(Map.class);
//		Map newMap = newExchange.getIn().getBody(Map.class);
//		logger.info(oldMap);
//		logger.info(newMap);
//		Properties aggregateBody = new Properties();
//		aggregateBody.putAll(oldMap);
//		aggregateBody.putAll(newMap);
		oldExchange.getIn().copyFrom(newExchange.getIn());;
//		logger.info(aggregateBody);
		return oldExchange;
	}

}
