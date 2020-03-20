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
	
	@Column(name="YEAR")
	private String year;
	
	@Column(name="MONTH")
	private String month;
	
	@Column(name="DAY")
	private String day;
	
	@Column(name="WEEKDAY")
	private String weekday;
	
	@Column(name="BREAKTIME")
	private String breakTime;

	@Column(name="OVERTIME")
	private String overTime;

	@Column(name="INTIME")
	private String inTime;
	
	@Column(name="OUTTIME")
	private String outTime;

	@Column(name="WORK_START_TIME")
	private String workStartTime;

	@Column(name="WORK_END_TIME")
	private String workEndTime;
	
	public int getTimeCardNo() {
        return this.timeCardNo;
    }
	public String getUserId() {
        return this.userId;
    }
	public String getDate() {
        return this.date;

    }
	public String getYear() {
        return this.year;
    }
	public String getMonth() {
        return this.month;
    }
	public String getDay() {
        return this.day;
    }
	public String getWeekDay() {
        return this.weekday;
    }
	public String getBreakTime() {
        return this.breakTime;
    }
	public String getOverTime() {
        return this.overTime;
    }
	public String getInTime() {
        return this.inTime;
    }
	public String getOutTime() {
        return this.outTime;
    }
	public String getWorkStartTime() {
        return this.workStartTime;
    }
	public String getWorkEndTime() {
        return this.workEndTime;
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
	public void setYear(String year) {
        this.year = year;
    }
	public void setMonth(String month) {
        this.month = month;
    }
	public void setDay(String day) {
        this.day = day;
    }
	public void setWeekDay(String weekday) {
        this.weekday = weekday;
    }
	public void setBreakTime(String breakTime) {
        this.breakTime = breakTime;
    }
	public void setOverTime(String overTime) {
        this.overTime = overTime;
    }
	public void setInTime(String inTime) {
		this.inTime = inTime;
	}
	public void setOutTime(String outTime) {
		this.outTime = outTime;
	}
	public void setworkStartTime(String workStartTime) {
        this.workStartTime = workStartTime;
    }
	public void setworkEndTime(String workEndTime) {
        this.workEndTime = workEndTime;
    }
}
	