package gw.bif.tagger.core.router;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gw.bif.tagger.core.aggregator.DefaultAggregationStrategy;

@Component
public class BacnetRouter extends RouteBuilder {

  Log logger = LogFactory.getLog(this.getClass());

  @Autowired
  ProducerTemplate producerTemplate;

  @Autowired
  DefaultAggregationStrategy aggregationStartegy;

  @Override
  public void configure() throws Exception {
    from("direct:in").process(new Processor() {
      @Override
      public void process(Exchange exchange) throws Exception {
        Map<String, String> rawAttributes = exchange.getIn().getBody(Map.class);
        Map<String, Map<String, String>> namespaceAttributesMap = new HashMap<>();
        rawAttributes.entrySet().stream().forEach((entry) -> {
          String[] namespaceAndProperty = entry.getKey().split(":");
          String namespace = namespaceAndProperty[0];
          Map<String, String> existingMappings = namespaceAttributesMap.getOrDefault(namespace, new HashMap<>());
          existingMappings.put(entry.getKey(), entry.getValue());
          namespaceAttributesMap.put(namespace, existingMappings);
        });
        exchange.getIn().setBody(namespaceAttributesMap, Map.class);
      }
    })
        .split()
        .method(AttributeMapSplitter.class, "split")
        .choice()
        .when((exchange) -> exchange.getIn().getBody(Map.class).containsKey("bacnet"))
        .process((exchange) -> System.out.println("bacnet: " + exchange.getIn().getBody(Map.class)))
        .when((exchange) -> exchange.getIn().getBody(Map.class).containsKey("modbus"))
        .process((exchange) -> System.out.println("modbus: " + exchange.getIn().getBody(Map.class)))
        .otherwise()
        .process((exchange) -> System.out.println("otherwise: " + exchange.getIn().getBody(Map.class)));

  }

  public Map<String, String> route(Map<String, String> message) {
    producerTemplate.sendBody("direct:in", message);
    return message;
  }

}
