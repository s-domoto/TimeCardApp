package main;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
/*
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
*/
@Repository
public interface TimeCardRepository extends JpaRepository<WorkingTimeEntity, String> {
	
	@Query(value="SELECT A.USERID, B.TIMECARDNO, B.DATE, COALESCE(B.INTIME, \"\") AS INTIME, COALESCE(B.OUTTIME, \"\") AS OUTTIME FROM USER_INFO A " + 
			"INNER JOIN TIMECARD B " + 
			"ON A.USERID = B.USERID " + 
			"WHERE " + 
			"A.USERID = :userId " + 
			"ORDER BY B.TIMECARDNO DESC LIMIT 1;", nativeQuery = true)
	List<WorkingTimeEntity> findWorkingTime(@Param("userId") String userId);
	
	@Query(value="INSERT INTO TIMECARD VALUES(0, :userId, :date, :inTime, :outTime);", nativeQuery = true)
	List<WorkingTimeEntity> InsertWorkingTime(@Param("userId") String userId,@Param("date") String date, @Param("inTime") String inTime, @Param("outTime") String outTime);
}
