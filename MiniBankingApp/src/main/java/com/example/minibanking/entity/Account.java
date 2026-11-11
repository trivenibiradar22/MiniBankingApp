
package com.example.minibanking.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "customer")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;
    private String role = "USER";
    public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	private BigDecimal balance = new BigDecimal("1000");

    public Long getId() {
        return id;
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
