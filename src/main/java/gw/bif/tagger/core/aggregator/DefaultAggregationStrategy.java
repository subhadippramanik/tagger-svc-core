package gw.bif.tagger.core.aggregator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DefaultAggregationStrategy implements AggregationStrategy {

	Log logger = LogFactory.getLog(this.getClass());

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		if (oldExchange == null) {
			logger.info("Return new exchange");
			return newExchange;
		}
		Map<String, String> oldMap = convertToMap(oldExchange.getIn().getBody(String.class));
		Map<String, String> newMap = convertToMap(newExchange.getIn().getBody(String.class));		
		Map<String, String> aggregateBody = new HashMap<>();
		aggregateBody.putAll(oldMap);
		aggregateBody.putAll(newMap);
		oldExchange.getIn().setBody(aggregateBody);
		return oldExchange;
	}

	public Map<String, String> convertToMap(String jsonString) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> map = new HashMap<>();			
			map = mapper.readValue(jsonString, new TypeReference<Map<String, String>>() {
			});
			return map;

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
