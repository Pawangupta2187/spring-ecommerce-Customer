package com.customer.customer.controller;

import com.customer.customer.entities.users.Customer;
import com.customer.customer.service.CustomerService;
import com.customer.customer.entities.users.DTO.AddressDTO;
import com.customer.customer.entities.users.DTO.CustomerProfileDTO;
import com.customer.customer.entities.users.DTO.UpdateCustomerProfileDTO;
import com.customer.customer.entities.users.DTO.UpdatePasswordDTO;
import com.customer.customer.exception.SuccessResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @GetMapping("/profile")
    public CustomerProfileDTO customerProfile(HttpServletRequest request) {
      return  customerService.getCustomerProfileByEmailId(request);
    }

    @PatchMapping("/profile")
    public ResponseEntity<SuccessResponse> updateProfile(HttpServletRequest request, @RequestBody @Valid UpdateCustomerProfileDTO updateCustomerProfileDTO) {
        return customerService.updateProfile(request,updateCustomerProfileDTO);
    }

    @GetMapping("/addresses")
    public List<AddressDTO> getCustomerAdresses(HttpServletRequest request) {
        return customerService.getCustomerAddresses(request);
    }



    @PostMapping("/address")
    public ResponseEntity<SuccessResponse>addAddress(HttpServletRequest request,@RequestBody @Valid  AddressDTO addressDTO) {
        return customerService.addAddress(request,addressDTO);
    }

    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<SuccessResponse> deleteAddresses(HttpServletRequest request,@PathVariable Long addressId) {
        return  customerService.deleteAddresses(request,addressId);
    }

    @PatchMapping("/address/{addressId}")
    public ResponseEntity<SuccessResponse> updateAdresses(HttpServletRequest request,@PathVariable Long addressId,@RequestBody @Valid AddressDTO addressDTO) {
        return customerService.updateAdresses(request,addressId,addressDTO);
    }


    @PutMapping("/updatepassword")
    public ResponseEntity<SuccessResponse> updatepassword(HttpServletRequest request,@RequestBody @Valid UpdatePasswordDTO updatePasswordDTO) {
        System.out.println(updatePasswordDTO.getPassword()+"--"+updatePasswordDTO.getConfirmPassword());
        return customerService.updatepassword(request,updatePasswordDTO);
    }

//    @Cacheable()
    @GetMapping("/user/{id}")
    public CustomerProfileDTO fetchUser(@PathVariable("id")String emailId){
        return customerService.findByEmailID(emailId);
        
    }
}
