package com.ad.teamnine.model;

import java.time.LocalDate;

public class MemberDTO {
    private String username;
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

	private Double height;
    private Double weight;
    private Integer age;
    private LocalDate birthdate;
    private String gender;
    private Double calorieIntake;
    private LocalDate registrationDate;
    // 可以根据需要添加其他字段

    public MemberDTO() {}

    // 添加一个构造函数，用于从Member对象创建DTO对象
    public MemberDTO(Member member) {
        this.username = member.getUsername();
        this.height = member.getHeight();
        this.weight = member.getWeight();
        this.age = member.getAge();
        this.birthdate = member.getBirthdate();
        this.gender = member.getGender();
        this.calorieIntake = member.getCalorieIntake();
        this.registrationDate = member.getRegistrationDate();
        // 如果需要的话，可以继续将其他字段从Member对象复制到DTO对象
    }

    // 添加getter和setter方法
    // ...
}