package com.everis.homework.mapper;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "USER_PROFILE")
public class Profile {
	public Profile() {
	}

	public Profile(String user, Rol rol) {
		this.user = user;
		this.roles = new CopyOnWriteArraySet<Rol>();
		this.roles.add(rol);
	}

	@Id
	@Column(name = "username", nullable = false)
	private String user;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "REL_ROL_USER", 
    joinColumns = 
      { @JoinColumn(name = "fk_user", referencedColumnName = "username") },
    inverseJoinColumns = 
      { @JoinColumn(name = "fk_rol", referencedColumnName = "id") })
	private Set<Rol> roles;
}
