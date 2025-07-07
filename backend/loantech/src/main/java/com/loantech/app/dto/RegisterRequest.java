package com.loantech.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RegisterRequest {
    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Email non valida")
    private String email;

    @NotBlank(message = "La password è obbligatoria")
    private String password;

    @NotBlank(message = "Il nome è obbligatorio")
    private String firstName;

    @NotBlank(message = "Il cognome è obbligatorio")
    private String lastName;

    @NotBlank(message = "Il codice fiscale è obbligatorio")
    @Pattern(regexp = "^[A-Z]{6}[0-9]{2}[A-Z][0-9]{2}[A-Z][0-9]{3}[A-Z]$",
            message = "Codice fiscale non valido")
    private String fiscalCode;

    private String phoneNumber;

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFiscalCode() { return fiscalCode; }
    public void setFiscalCode(String fiscalCode) { this.fiscalCode = fiscalCode; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}