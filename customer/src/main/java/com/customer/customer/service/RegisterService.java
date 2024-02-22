package com.customer.customer.service;

import com.customer.customer.entities.users.*;
import com.customer.customer.exception.*;
import com.customer.customer.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.*;

@Service
public class RegisterService {

    @Autowired
    RegisterCustomerRepository registerCustomerRepository;

      @Autowired
    RoleRepository roleRepository;
//
    @Autowired
    UserRepository userRepository;
    @Autowired
    FileUploadUtil fileUploadUtil;
    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;
//
    @Autowired
    EmailSenderService emailSenderService;


    public ResponseEntity<SuccessResponse> createCustomer(Customer customer) throws IOException {
        if(!customer.getPassword().equals(customer.getConfirmPassword()))
            throw new BadRequestException("Password and ConfirmPassword field not match");
        if(checkUserExist(customer.getEmailId())!=null) {
            throw new ConflictException("Customer All ready Exist");
        }
        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findByAuthority("ROLE_CUSTOMER").get(0);
        customer.getAddresses().forEach(customer::addAddress);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        roles.add(role);
        customer.setRoles(roles);
        //String imageName = StringUtils.cleanPath(image.getOriginalFilename());
       // customer.setImage(imageName);
//upload image

        //  FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        try {
            registerCustomerRepository.save(customer);
//            String uploadDir = "user-photos/" +customer.getId();
//            fileUploadUtil.saveFile(uploadDir,imageName,image);
        }catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }

        ConfirmationToken confirmationToken = new ConfirmationToken(customer);
        confirmationTokenRepository.save(confirmationToken);
        // SimpleMailMessage mailMessage = new SimpleMailMessage();
        SimpleMailMessage mailMessage=emailSenderService.CreateBodyForMail(customer.getEmailId(),"Activate Account!","To activate your account, please click here : "
                +"http://localhost:8080/activate-account?token="+confirmationToken.getConfirmationToken());

        emailSenderService.sendEmail(mailMessage);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResponse("Account is created for activate check your mail"));

    }


    public ResponseEntity<SuccessResponse>activateCustomerAccount(String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);
        if(token != null) {
            if(token.getExpiryDate().compareTo(new Date()) < 0) {
                confirmationTokenRepository.delete(token);
                ConfirmationToken newToken = new ConfirmationToken(token.getUser());
                confirmationTokenRepository.save(newToken);
                SimpleMailMessage mailMessage =emailSenderService.CreateBodyForMail(newToken.getUser().getEmailId()
                        ,"Activate Account!","To activate your account, please click here : "
                                +"http://localhost:8080/register/activateaccount?token="+newToken.getConfirmationToken());
                emailSenderService.sendEmail(mailMessage);
                throw new BadRequestException("Your token is Expire a new link is sent on your mail");
            } else {
                Customer customer = findCustomerByMail(token.getUser().getEmailId());
                if (customer.getIsActive()) {
                    return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse("Account is already active"));
                }
                customer.setIsActive(true);
                confirmationTokenRepository.delete(token);
                return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResponse("Account Successfully Activate"));
            }
        }
        else {
            throw new TokenFailedException("Token is Invalid");
        }
    }

    public ResponseEntity<SuccessResponse>resendActivationLink(String email){
        Customer customer=findCustomerByMail(email);
        if(customer==null){//System.out.println("user");
            throw new NotFoundException("user not found");
        }
        if(customer.getIsActive())
            throw new ConflictException("Customer all ready active");
        if(confirmationTokenRepository.findByUserId(customer.getId())!=null) {
            ConfirmationToken oldtoken=confirmationTokenRepository.findByUserId(customer.getId());
            try {
                confirmationTokenRepository.delete(oldtoken);
            }catch (Exception ex)
            {
                throw new BadRequestException(ex.getMessage());
            }
        }
        ConfirmationToken confirmationToken = new ConfirmationToken(customer);
        confirmationTokenRepository.save(confirmationToken);
        SimpleMailMessage mailMessage = emailSenderService.CreateBodyForMail(customer.getEmailId(),
                "Activate Account!","To activate your account, please click here : "
                        +"http://localhost:8080/activate-account?token="+confirmationToken.getConfirmationToken() );
        emailSenderService.sendEmail(mailMessage);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResponse("Link is sent to your mail"));
    }

    //changepassword
    public void changePassword(Long id, String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Optional<Customer> customer=registerCustomerRepository.findById(id);
        if(customer.isPresent())
        {
            customer.get().setPassword(passwordEncoder.encode(password));
           try{
            registerCustomerRepository.save(customer.get());
        }catch (Exception ex) {
            ex.printStackTrace();
            throw new BadRequestException(ex.getMessage());
        }

        }else
        {
            throw new NotFoundException("user not found");
        }

    }

    public Customer findCustomerByMail(String email) {
        Customer customer = registerCustomerRepository.findCustomerByemailId(email);
        if (customer!=null)
            return customer;
        else
            return null;
    }
    public User checkUserExist(String email) {
        List<User> user = userRepository.findUserByemailId(email);
        if (user.size() > 0)
            return user.get(0);
        else
            return null;
    }

}

