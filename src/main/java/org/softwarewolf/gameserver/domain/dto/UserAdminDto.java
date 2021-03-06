package org.softwarewolf.gameserver.domain.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.softwarewolf.gameserver.domain.User;

public class UserAdminDto implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<UserListItem> userList;
	private String selectedUserId;
	private User selectedUser;
	private String password;
	private String verifyPassword;
	public Map<String, String> allRoles;
	public Map<String, String> selectedRoles;	

	public UserAdminDto() {}
	
	public List<UserListItem> getUserList() {
		return userList;
	}

	public void setUserList(List<UserListItem> userList) {
		this.userList = userList;
	}
	
	public String getSelectedUserId() {
		return selectedUserId;
	}
	
	public void setSelectedUserId(String selectedUserId) {
		 this.selectedUserId = selectedUserId;
	}
	
	public User getSelectedUser() {
		return selectedUser;
	}
	
	public void setSelectedUser(User selectedUser) {
		 this.selectedUser = selectedUser;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public String getVerifyPassword() {
		return verifyPassword;
	}
	
	public void setVerifyPassword(String verifyPassword) {
		this.verifyPassword = verifyPassword;
	}
	
	public Map<String, String> getAllRoles() {
		return allRoles;
	}

	public void setAllRoles(Map<String, String> allRoles) {
		this.allRoles = allRoles;
	}

	public Map<String, String> getSelectedRoles() {
		return selectedRoles;
	}

	public void setSelectedRoles(Map<String, String> selectedRoles) {
		this.selectedRoles = selectedRoles;
	}
}
