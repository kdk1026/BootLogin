--자동로그인
CREATE TABLE persistent_logins (
	username 		VARCHAR(64), 	--아이디
    session_id 		VARCHAR(64), 	--자동로그인 세션 ID
    session_limit 	TIMESTAMP, 		--세션 ID 유효시간
    PRIMARY KEY (username)		
);