package kr.co.test.bcrypt;

import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 2018. 12. 24.
 * @author 김대광
 * <pre>
 * -----------------------------------
 * 개정이력
 * 2018. 12. 24. 김대광	최초작성
 * </pre>
 */
public class BCryptTest {
	
	private static final Logger logger = LoggerFactory.getLogger(BCryptTest.class);
	
	@Test
	public void test() {
		String password = "admin!@34";
		
		String passwordHashed = BCrypt.hashpw(password, BCrypt.gensalt());
		
		logger.debug("bcrypt: {}", passwordHashed);
		logger.debug("valid: {}", BCrypt.checkpw(password, passwordHashed));		
	}
	
}
