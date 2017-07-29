package gw.bif.tagger.core.router;

import java.util.Map;

import javax.inject.Inject;

import org.apache.camel.ExchangeProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class NamespaceRouter {
	
	private static final Log LOGGER = LogFactory.getLog(NamespaceRouter.class);
	
	private static final String NAMESPACE_URL_PREFIX = "namespace.";
	
	private final ApplicationContext context;
	
	@Inject
	public NamespaceRouter(ApplicationContext context) {
		this.context = context;
	}

	public String getRoute(@ExchangeProperties Map<String, Object> properties){
		String namespace = (String) properties.get("namespace");
		if(StringUtils.isEmpty(namespace)){
			return null;
		}
		properties.put("namespace", StringUtils.EMPTY);
		String protocolService = context.getEnvironment().getProperty(NAMESPACE_URL_PREFIX + namespace.toLowerCase());
		if(StringUtils.isEmpty(protocolService)){
			LOGGER.error("getRoute: No tagger service is configured for the protocol: "+ protocolService);
			return null;
		}
		return protocolService;
	}

}
