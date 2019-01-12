package config.mvc.resolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import common.util.map.ParamMap;

/**
 * <pre>
 * 파라미터 수집 클래스
 *  - HttpServletRequest
 *  - Header Map, Parameter Map
 *  
 * 	- HandlerMethodArgumentResolver 에서 Map 객체 사용
 *  - Component 메소드 인자
 * </pre>
 * @since 2018. 12. 24.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 24. 김대광	최초작성
 * </pre>
 */
public class ParamCollector {

	private HttpServletRequest request;
	private ParamMap map = new ParamMap();
	private ParamMap heardMap = new ParamMap();
	
	public HttpServletRequest getRequest() {
		return request;
	}

	protected void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public ParamMap getMap() {
		return map;
	}

	public void setMap(ParamMap map) {
		this.map = map;
	}
	
	public ParamMap getHeardMap() {
		return heardMap;
	}

	public void setHeardMap(ParamMap heardMap) {
		this.heardMap = heardMap;
	}

	public Object get(String key) {
		return map.get(key);
	}

	public void put(String key, Object value) {
		map.put(key, value);
	}

    public void remove(String key){
        map.remove(key);
    }

    public void putAll(Map<? extends String, ? extends Object> m) {
    	map.putAll(m);
    }
    
    public boolean containsKey(String key) {
		return map.containsKey(key);
	}

	public String getString(String key) {
		return map.getString(key);
	}
	
	public String getString(String key, String defaultVal) {
		if ( !map.containsKey(key) ) {
			return defaultVal;
		}
		
		String val = map.getString(key);
		if ( StringUtils.isEmpty(val) ) {
			return defaultVal;
		}
		
		return val;
	}
	
	public int getInt(String key) {
		return map.getInteger(key);
	}

	public double getDouble(String key) {
		return map.getDouble(key);
	}
	
	public boolean getBoolean(String key) {
		return map.getBoolean(key);
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getArray(String key) {
		List<Object> list = null;
		try {
			list = (List<Object>) map.get(key);
			
		} catch (ClassCastException e) {
			list = new ArrayList<>();
			list.add(map.get(key));
		}
		return list;
	}
	
	public MultipartFile getMultipartFile(String key) {
		return (MultipartFile) map.get(key);
	}
	
	public ParamMap getMapAll() {
		ParamMap paramMap = this.getHeardMap();
		paramMap.putAll(this.map);
		return paramMap;
	}

	@Override
	public String toString() {
		return map.toString();
	}

}
