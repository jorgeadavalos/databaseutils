package com.assoc.jad.database.test;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
//@IdClass(UserKeys.class)
public class ServiceUsers implements Serializable {
	private static final long serialVersionUID = 1L;
	//@Id
	private String service;
    private String loginid;

    @Id
	private int id;
	private String firstname;
	private String lastname;
	private String password;
	private String access;
	private String email;
	private int failedcount;
	private long failedtime;
	private long createdate;
	private String goodemail;
	
	public ServiceUsers() {
	}

	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAccess() {
		return access;
	}
	public void setAccess(String access) {
		this.access = access;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getFailedcount() {
		return failedcount;
	}
	public void setFailedcount(Integer failedcount) {
		this.failedcount = failedcount;
	}
	public long getFailedtime() {
		return failedtime;
	}
	public void setFailedtime(Long failedtime) {
		this.failedtime = failedtime;
	}
	public long getCreatedate() {
		return createdate;
	}
	public void setCreatedate(Long createdate) {
		this.createdate = createdate;
	}
	public String getGoodemail() {
		return goodemail;
	}
	public void setGoodemail(String goodemail) {
		this.goodemail = goodemail;
	}
	public String getLoginid() {
		return loginid;
	}
	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}

	@Override
	public String toString() {
		return this.email+" "+this.loginid+" "+this.lastname;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public int getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
