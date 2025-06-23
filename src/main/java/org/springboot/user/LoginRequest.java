package org.springboot.user;

public class LoginRequest {
    private String email;
    private String mobile;
    private String password;
    private boolean forceLogin;
    
 // getters and setters
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isForceLogin() {
		return forceLogin;
	}
	public void setForceLogin(boolean forceLogin) {
		this.forceLogin = forceLogin;
	}
	
	

    
}