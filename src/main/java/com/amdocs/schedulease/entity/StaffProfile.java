package com.amdocs.schedulease.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "staff_profile")
public class StaffProfile {

    @Id
    private Long id; // Same as user_account.id

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private UserAccount userAccount;

    @Column(nullable = false, length = 120)
    private String department;

    @Column(nullable = false)
    private LocalDate dob;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Version
    private Integer version = 0;

    // Constructors
    public StaffProfile() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
