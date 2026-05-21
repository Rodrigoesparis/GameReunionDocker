package com.rodrigo.modelo;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUser;
    //Nombre de la persona
    private String name;
    //Nombre de la cuenta
    @Column(unique = true)
    private String username;
    
    private String email;

    private String password;

    private Integer age;

	@Column(name = "karma", nullable = false)
	private Integer karma = 1000;

    //Relacion con el grupo para creador
    @OneToMany(mappedBy = "creator")
    @JsonIgnore
    private List<GameReunion> createdGroups;

    //Relacion usuarios con participantes
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Participant> participants;

    //Relacion de usuario con las peticiones
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Request> requests;

    //Relacion de decision de la petición
    @OneToMany(mappedBy = "decidedBy")
    @JsonIgnore
    private List<Request> requestsDecided;

	@Column(nullable = true)
	private String country;

	@Column(nullable = true)
	private String timezone;

	@Column(columnDefinition = "TEXT", nullable = true)
	private String bio;

	@Column(name = "call_style", columnDefinition = "TEXT", nullable = true)
	private String callStyle;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Platform> platforms;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Games> games;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Lenguage> languages;

	public Integer getIdUser() {
		return idUser;
	}
	public void setIdUser(Integer idUser) {
		this.idUser = idUser;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public List<GameReunion> getCreatedGroups() {
		return createdGroups;
	}
	public void setCreatedGroups(List<GameReunion> createdGroups) {
		this.createdGroups = createdGroups;
	}
	public List<Participant> getParticipants() {
		return participants;
	}
	public void setParticipants(List<Participant> participants) {
		this.participants = participants;
	}
	public List<Request> getRequests() {
		return requests;
	}
	public void setRequests(List<Request> requests) {
		this.requests = requests;
	}
	public List<Request> getRequestsDecided() {
		return requestsDecided;
	}
	public void setRequestsDecided(List<Request> requestsDecided) {
		this.requestsDecided = requestsDecided;
	}
	public Integer getKarma() {
	 	return karma; 
	}
	public void setKarma(Integer karma) {
		 this.karma = karma; 
		}

	public List<Platform> getPlatforms() { return platforms; }
	public void setPlatforms(List<Platform> platforms) { this.platforms = platforms; }

	public List<Games> getGames() { return games; }
	public void setGames(List<Games> games) { this.games = games; }

	public List<Lenguage> getLanguages() { return languages; }
	public void setLanguages(List<Lenguage> languages) { this.languages = languages; }

	public String getCountry() {
    return country;
}
public void setCountry(String country) {
    this.country = country;
}

public String getTimezone() {
    return timezone;
}
public void setTimezone(String timezone) {
    this.timezone = timezone;
}

public String getBio() {
    return bio;
}
public void setBio(String bio) {
    this.bio = bio;
}

public String getCallStyle() {
    return callStyle;
}
public void setCallStyle(String callStyle) {
    this.callStyle = callStyle;
}
	}
	