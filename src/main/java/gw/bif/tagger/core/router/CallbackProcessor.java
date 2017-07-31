package gw.bif.tagger.core.router;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class CallbackProcessor implements Processor{
	
	Log log = LogFactory.getLog(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		Map<String, Object> resultMessage = new HashMap<>();
		@SuppressWarnings("unchecked")
		Map<String, Object> messageBody = exchange.getIn().getBody(Map.class);
		String value = messageBody.remove("value").toString();
		resultMessage.put("value", value);
		resultMessage.put("attributes", messageBody);
		log.info(resultMessage.toString());
	}

}
