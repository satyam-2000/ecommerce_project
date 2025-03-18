package com.example.ecommerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    @Size(min = 3, message = "Street name must contains at least 3 characters")
    private String streetName;

    @NotBlank
    @Size(min = 6, message = "Pincode must contains at least 6 characters")
    private String pinCode;

    @NotBlank
    @Size(min = 3, message = "City must contains at least 3 characters")
    private String city;

    @NotBlank
    @Size(min = 3, message = "Country must contains at least 3 characters")
    private String country;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
