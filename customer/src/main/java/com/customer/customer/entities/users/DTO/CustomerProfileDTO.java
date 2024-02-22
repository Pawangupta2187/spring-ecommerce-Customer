package com.customer.customer.entities.users.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
@Getter
@Setter
@NoArgsConstructor
@Data
//@CacheConfig(cacheNames = "employees")
public class CustomerProfileDTO implements Serializable {
    private String emailId;
    private String firstName;
    private String middleName;
    private String lastName;
    private Boolean isActive;
    private String Contact;
  //  private String image;


    public CustomerProfileDTO(String emailId, String firstName, String middleName, String lastName, Boolean isActive, String Contact) {
        this.emailId = emailId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.isActive = isActive;
        this.Contact = Contact;
    }
}
