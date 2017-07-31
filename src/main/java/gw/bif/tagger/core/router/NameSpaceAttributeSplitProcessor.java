package gw.bif.tagger.core.router;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;


@Component
public class NameSpaceAttributeSplitProcessor implements Processor{

	@SuppressWarnings("unchecked")
	@Override
	public void process(Exchange exchange) throws Exception {
		Map<String, Object> rawMessage = exchange.getIn().getBody(Map.class);
		Map<String, Map<String, Object>> namespaceAttributesMap = new HashMap<>();
		//TODO check if no :, then do nothing
		rawMessage.entrySet().stream().filter(entry -> !entry.getKey().equals("attributes")).forEach((entry) -> {
			String[] namespaceAndProperty = entry.getKey().split(":");
			String namespace = namespaceAndProperty[0];
			Map<String, Object> existingMappings = namespaceAttributesMap.getOrDefault(namespace,
					new HashMap<>());
			existingMappings.put(entry.getKey(), entry.getValue());
			namespaceAttributesMap.put(namespace, existingMappings);
		});
		((Map<String, Object>) rawMessage.get("attributes")).entrySet().stream().forEach((entry) -> {
			String[] namespaceAndProperty = entry.getKey().split(":");
			String namespace = namespaceAndProperty[0];
			Map<String, Object> existingMappings = namespaceAttributesMap.getOrDefault(namespace,
					new HashMap<>());
			existingMappings.put(entry.getKey(), entry.getValue());
			namespaceAttributesMap.put(namespace, existingMappings);
		});
		exchange.getIn().setBody(namespaceAttributesMap);
	}

}
