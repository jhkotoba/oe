package jkt.oe.config.thymeleaf;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring6.view.reactive.ThymeleafReactiveViewResolver;

import jkt.oe.config.constant.GatewayConst;

/**
 * Thymeleaf 뷰 리졸버 설정을 위한 BeanPostProcessor 구현체
 */
@Component
public class ThymeleafConfig implements BeanPostProcessor {
	
	/**
     * 빈 초기화 후 추가 처리 메서드
     * @param bean 초기화된 빈 객체
     * @param beanName 빈 이름
     * @return 빈 객체 그대로 반환
     * @throws BeansException 처리 중 예외 발생 시
     */
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
