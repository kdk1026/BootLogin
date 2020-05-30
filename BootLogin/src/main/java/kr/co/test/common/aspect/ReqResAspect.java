package kr.co.test.common.aspect;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import common.LogDeclare;
import common.util.FormattingUtil;
import common.util.RequestIpUtil;
import common.util.json.JacksonUtil;
import common.util.map.ParamMap;
import config.mvc.resolver.ParamCollector;

/**
 * @since 2019. 1. 3.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2019. 1. 3. 김대광	최초작성
 * </pre>
 */
@Component
@Aspect
public class ReqResAspect extends LogDeclare {

//	@Pointcut("within(@org.springframework.stereotype.Controller *)")
//    public void controller() {}
	
	@Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
	public void restControllerBean() {}
	
//	@Around("controllerBean() || restControllerBean()")
	@Around("restControllerBean()")
	public Object preProcess(ProceedingJoinPoint jp) throws Throwable {
        Object[] values = jp.getArgs();
        
        ParamCollector paramCollector = new ParamCollector();
        for (Object obj : values) {
        	if (obj instanceof ParamCollector) {
        		paramCollector = (ParamCollector)obj;
        	}
        }

        ParamMap retMap = new ParamMap();

    	// 요청 로깅
    	this.viewReqLog(paramCollector);

        if ( !retMap.isEmpty() ) {
        	return retMap;
        }

    	Object result = jp.proceed();

    	// 응답 로깅
//    	this.viewResLog(paramCollector, result);

    	return result;
	}
	
	/**
	 * 요청 로깅
	 * @param paramCollector
	 */
	public void viewReqLog(ParamCollector paramCollector) {
		HttpServletRequest request = paramCollector.getRequest();

		if ( request != null ) {
			String sUri = request.getRequestURI();

			ParamMap headerMap = paramCollector.getHeardMap();
			ParamMap paramMap = paramCollector.getMap();

			String sParam = "";
			String sHeader = "";

			// 헤더
			if ( !headerMap.isEmpty() ) {
				sHeader = JacksonUtil.converterMapToJsonStr(headerMap);
			}

			// 파라미터
			if ( !paramMap.isEmpty() ) {
				ParamMap logParamMap = new ParamMap();
				logParamMap.putAll(paramMap);
				
				if ( logParamMap.containsKey("pw") ) {
					String sPw = logParamMap.getString("pw");
					logParamMap.put("pw", FormattingUtil.passwordMasking(sPw) );
				}
				
				sParam = JacksonUtil.converterMapToJsonStr(logParamMap);
			}
			
			String sReqIp = RequestIpUtil.getIpAdd(request);
			sReqIp = FormattingUtil.makeIpv4AddrMasking(sReqIp);
			
			Map<String, Object> loggingMap = new LinkedHashMap<>();
			loggingMap.put("ClientIp", 			sReqIp);
			loggingMap.put("RequestUri", 		sUri);
			loggingMap.put("RequestHeader", 	sHeader);
			loggingMap.put("RequestParam", 		sParam);
			
			logger.info("{}", loggingMap);
		}
	}
	
}
