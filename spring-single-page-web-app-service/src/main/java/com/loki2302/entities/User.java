package com.loki2302.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(unique = true)
	private String userName;
	private String password;
	
	@OneToMany(mappedBy = "author")
	private List<Post> posts;
	
	@OneToMany(mappedBy = "user")
	private List<Session> sessions;
	
	public Long getId() {
		return id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}  
	
	@Override
	public boolean equals(Object obj) {
	    if(!(obj instanceof User)) {
	        return false;
	    }
	    
	    User other = (User)obj;
	    
        return id == other.getId();
	}
}