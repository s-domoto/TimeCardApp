package main;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="USER_INFO")
public class UserInfoEntity {
	
	@Id
	@Column(name="USERID")
	private String userId;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="PASS")
	private String pass;
	
	public String getUserID() {
        return this.userId;
    }
	public String getName() {
        return this.name;
    }
	public String getPass() {
        return this.pass;
    }
	public void setTimeCardNo(String userId) {
		this.userId = userId;
	}
	public void setInTime(String name) {
		this.name = name;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
}
	