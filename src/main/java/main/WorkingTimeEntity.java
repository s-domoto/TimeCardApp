package main;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
@Table(name="TIMECARD")
public class WorkingTimeEntity {
	
	@Id
	@Column(name="TIMECARDNO")
//	@GeneratedValue
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	private int timeCardNo;
	
	@Column(name="USERID")
	private String userId;
	
	@Column(name="DATE")
	private String date;
	
	
	@Column(name="INTIME")
	private String inTime;
	
	@Column(name="OUTTIME")
	private String outTime;
	
//	public WorkingTimeEntity() {
//		super();
//	}
	
//	public WorkingTimeEntity(String inTime, String outTime) {
//		super();
//		this.inTime = inTime;
//		this.outTime = outTime;
//		
//	}
	
	public int getTimeCardNo() {
        return this.timeCardNo;
    }
	public String getUserId() {
        return this.userId;
    }
	public String getDate() {
        return this.date;
    }
	public String getInTime() {
        return this.inTime;
    }
	public String getOutTime() {
        return this.outTime;
    }
/*	public void setTimeCardNo(String timeCardNo) {
		this.timeCardNo = timeCardNo;
	}
*/
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public void setInTime(String inTime) {
		this.inTime = inTime;
	}
	public void setOutTime(String outTime) {
		this.outTime = outTime;
	}
}
	