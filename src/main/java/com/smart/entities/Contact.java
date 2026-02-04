package com.smart.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="CONTACT")
public class Contact {

	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	private int cId;
	private String name;
	private String nickName;
	private String email;
	private String image;
	private String work;
	private String phone;
	@Column(length = 1000)
	private String discriptions;
	
	@ManyToOne()
	private User user;
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
//	public Contact() {
//		super();
//		// TODO Auto-generated constructor stub
//	}
	public int getcId() {
		return cId;
	}
	public void setcId(int cId) {
		this.cId = cId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getWork() {
		return work;
	}
	public void setWork(String work) {
		this.work = work;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getDiscriptions() {
		return discriptions;
	}
	public void setDiscriptions(String discriptions) {
		this.discriptions = discriptions;
	}
	@Override
	public String toString() {
		return "Contact [cId=" + cId + ", name=" + name + ", nickName=" + nickName + ", email=" + email + ", image="
				+ image + ", work=" + work + ", phone=" + phone + ", discriptions=" + discriptions + ", user=" + user
				+ "]";
	}
	
	
}
