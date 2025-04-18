package jkt.oe.config.thymeleaf;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring6.view.reactive.ThymeleafReactiveViewResolver;

import jkt.oe.config.constant.GatewayConst;

@Component
public class ThymeleafConfig implements BeanPostProcessor {
	
	@Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ThymeleafReactiveViewResolver resolver) {
        	
            Map<String, Object> vars = new HashMap<>();
            vars.put("gatewayPrefix", GatewayConst.OE_PREFIX_NAME);
            resolver.setStaticVariables(vars);
        }
        return bean;
    }

}
