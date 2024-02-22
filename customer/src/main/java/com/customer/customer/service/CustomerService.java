package com.customer.customer.service;


import com.customer.customer.entities.users.*;
import com.customer.customer.entities.users.DTO.AddressDTO;
import com.customer.customer.entities.users.DTO.CustomerProfileDTO;
import com.customer.customer.entities.users.DTO.UpdateCustomerProfileDTO;
import com.customer.customer.entities.users.DTO.UpdatePasswordDTO;
import com.customer.customer.exception.*;
import com.customer.customer.repository.AddressRepository;
import com.customer.customer.repository.RegisterCustomerRepository;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    RegisterCustomerRepository registerCustomerRepository;



    @Autowired
    ModelMapper mm;

    @Autowired
    TokenStore tokenStore;

    @Autowired
    AddressRepository addressRepository;


    @Autowired
    RegisterService registerService;

//    @Autowired                    //use this controllers service using rest service
//    RegisterController registerController;

    public CustomerProfileDTO getCustomerProfileByEmailId(HttpServletRequest request)
    {
        Principal principal = request.getUserPrincipal();
        String emailId=principal.getName();
        CustomerProfileDTO customer=registerCustomerRepository.customerProfile(emailId);
            if(customer==null)
                throw new NotFoundException("User Not FOund");
            return customer;
    }

    public List<AddressDTO>getCustomerAddresses(HttpServletRequest request)
    {
        String authHeader=request.getHeader("Authorization");
        String tokenValue = authHeader.replace("Bearer", "").trim();
        OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
        Principal principal = request.getUserPrincipal();
        String emailId=principal.getName();
        if(!accessToken.isExpired()) {
            Customer customer=registerCustomerRepository.findCustomerByemailId(emailId);
            List<AddressDTO>addresses=addressRepository.findAddressesByCutomerId(customer.getId());
            System.out.println("eeeee"+addresses.toString());

           // Set<AddressDTO> addressdto=new HashSet<AddressDTO>() ;
            return addresses;
        }
        throw new TokenFailedException("Invalid Token or Token Expire");
    }
    public ResponseEntity<SuccessResponse> updatepassword(HttpServletRequest request, @RequestBody UpdatePasswordDTO updatePasswordDTO)
    {

        Principal principal = request.getUserPrincipal();
        String emailId=principal.getName();

            if (!updatePasswordDTO.getPassword().equals(updatePasswordDTO.getConfirmPassword()))
                throw new ConflictException("Password and ConfirmPassword field not match");
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

           //List<Customer> customers = registerCustomerRepository.findCustomerByemailId(emailId);

            Customer customer=registerCustomerRepository.findCustomerByemailId(emailId);
        if(customer==null)
            throw new NotFoundException("User not found");
            registerService.changePassword(customer.getId(), updatePasswordDTO.getPassword());
       // registerController.logout(request);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("Password Changed"));

    }


    public ResponseEntity<SuccessResponse>addAddress(HttpServletRequest request,AddressDTO addressDTO)
    {
        Principal principal = request.getUserPrincipal();
        String emailId=principal.getName();
        Customer customer = registerCustomerRepository.findCustomerByemailId(emailId);
        if (customer!=null) {
           // Optional<Customer> customer=registerCustomerRepository.findById(customer.getId());
            Address address=new Address();
            mm.map(addressDTO, address);
            customer.addAddress(address);
            try{
                registerCustomerRepository.save(customer);
            }catch (Exception ex){
                throw new BadRequestException(ex.getMessage());
             }
                   return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResponse("Address add Succefully"));
        } else
                throw new NotFoundException("Invalid User");

    }


    public ResponseEntity<SuccessResponse>updateProfile(HttpServletRequest request, UpdateCustomerProfileDTO updateCustomerProfileDTO)
    {
        Principal principal = request.getUserPrincipal();
        String emailId=principal.getName();
        Customer customer=registerCustomerRepository.findCustomerByemailId(emailId);
        if(customer==null)
            throw new NotFoundException("Not found");
        mm.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        mm.map(updateCustomerProfileDTO, customer);
        try{
            registerCustomerRepository.save(customer);
        }catch (Exception ex) {
            ex.printStackTrace();
           throw new BadRequestException(ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("Profile updated"));

    }

    public ResponseEntity<SuccessResponse> deleteAddresses(HttpServletRequest request,@RequestParam Long addressid)
    {
        Principal principal = request.getUserPrincipal();
        String emailId=principal.getName();
        Customer customer=registerCustomerRepository.findCustomerByemailId(emailId);
       if(customer==null)
           throw new NotFoundException("Customer Not Found");

        List<Address>addresses=addressRepository.findAddressesByCutomerIdANDAddressId(customer.getId(),addressid);

        if(addresses.size()>0 && !addresses.get(0).getIsDelete())
        {
//            if(addresses.get(0).getIsDelete())
//                throw new NotFoundException("address id not valid");
            Address address=addresses.get(0);
            address.setIsDelete(true);
            try{
                addressRepository.save(address);
            }catch (Exception ex) {
                throw new BadRequestException(ex.getMessage());
            }
            return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("Address Deleted"));
        }
        else
        {
            throw new NotFoundException("Address id not valid");
        }

    }


    public ResponseEntity<SuccessResponse> updateAdresses(HttpServletRequest request,@RequestParam Long addressid,@RequestBody AddressDTO addressDTO)
    {
        Principal principal = request.getUserPrincipal();
        String emailId=principal.getName();
        Customer customer=registerCustomerRepository.findCustomerByemailId(emailId);
        List<Address>addresses=addressRepository.findAddressesByCutomerIdANDAddressId(customer.getId(),addressid);
        if(addresses.size()>0 && !addresses.get(0).getIsDelete())
        {
            Address address=addresses.get(0);
            mm.getConfiguration().setPropertyCondition(Conditions.isNotNull());
            mm.map(addressDTO, address);
            try{
                addressRepository.save(address);
            }catch (Exception ex) {
                throw new BadRequestException(ex.getMessage());
            }
            return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("Address Updated"));
        }
        else
        {
            throw new NotFoundException("Address id not valid");
        }

    }



    @Cacheable(value="customer", key="#email")
   public CustomerProfileDTO findByEmailID(String email){
        System.out.println("Called DataBase for "+email);
       CustomerProfileDTO customer=registerCustomerRepository.customerProfile(email);
       if(customer==null)
           throw new NotFoundException("User Not FOund");
       return customer;
    }


//    @SuppressWarnings("unchecked")
//    @Transactional
//    public List<Customer> getCustomerAddressesByEmailId(Long id)
//    {
//        List<Customer>addresses=registerCustomerRepository.getCustomerAddressesById(id);
//        return addresses;
//    }
//    public Customer addCustomerAddress(Long id, AddressDTO addressDTO)
//    {
//        Optional<Customer> customer=registerCustomerRepository.findById(id);
//        Address address=new Address();
//        mm.map(addressDTO, address);
//        customer.get().addAddress(address);
//        registerCustomerRepository.save(customer.get());
//        return customer.get();
//    }
}
