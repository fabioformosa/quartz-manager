package it.fabioformosa.quartzmanager.security.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="Authority")
public class Authority implements GrantedAuthority {

	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name="name")
	String name;

	@Override
	public String getAuthority() {
		return name;
	}

	@JsonIgnore
	public Long getId() {
		return id;
	}

	@JsonIgnore
	public String getName() {
		return name;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

}
