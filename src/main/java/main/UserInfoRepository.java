package main;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfoEntity, String> {
	
	@Query(value="SELECT USERID, NAME, PASS FROM USER_INFO WHERE USERID = :userId AND PASS = :pass", nativeQuery = true)
	List<UserInfoEntity> findUserInfo(@Param("userId") String userId, @Param("pass") String pass);
	
	@Query(value="SELECT USERID, NAME, PASS FROM USER_INFO WHERE USERID = :userId", nativeQuery = true)
	List<UserInfoEntity> findUserInfo2(@Param("userId") String userId);
/*	
	@Query(value="SELECT * FROM USER_INFO " + 
			"INNER JOIN TIMECARDb" + 
			"ON USER_INFO.USERID = TIMECARD.USERID " + 
			"WHERE USER_INFO.USERID = :user_id;", nativeQuery = true)
	List<UserInfoEntity> findByuserId(@Param("userId") int userId);
*/
}
