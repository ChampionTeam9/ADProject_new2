package com.ad.teamnine.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
 
@Entity
@Table(name="Users")
public abstract class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Column
	@Size(min = 3, message = "username must be at least 3 characters")
	private String username;
	@Column
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*\\p{Punct}).{8,}$", message = "Password must be at least 8 characters long, "
			+ "contains a number and have at least one punctuation.")
	private String password;

	public User() {}
	
	public User(int id, String username, String password) {
		this.id = id;
		this.username = username;
		this.password = password;
	}

	// getter and setter
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
