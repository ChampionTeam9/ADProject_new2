package com.ad.teamnine.model;

import java.time.LocalDate;

public class MemberDTO {
    private String username;
	private Double height;
    private Double weight;
    private Integer age;
    private LocalDate birthdate;
    private String gender;
    private Double calorieIntake;
    private LocalDate registrationDate;
    

    public MemberDTO() {}

    
    public MemberDTO(Member member) {
        this.username = member.getUsername();
        this.height = member.getHeight();
        this.weight = member.getWeight();
        this.age = member.getAge();
        this.birthdate = member.getBirthdate();
        this.gender = member.getGender();
        this.calorieIntake = member.getCalorieIntake();
        this.registrationDate = member.getRegistrationDate();
        
    }

    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public LocalDate getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(LocalDate birthdate) {
		this.birthdate = birthdate;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Double getCalorieIntake() {
		return calorieIntake;
	}

	public void setCalorieIntake(Double calorieIntake) {
		this.calorieIntake = calorieIntake;
	}

	public LocalDate getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(LocalDate registrationDate) {
		this.registrationDate = registrationDate;
	}
}