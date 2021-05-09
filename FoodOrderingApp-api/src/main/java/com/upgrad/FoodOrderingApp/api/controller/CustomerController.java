package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/*This is a Rest Controller Which handles two endpoints :
1. Get All Categories - “/category” : Getting All the categories from the database.
It is a GET request and will not require any parameters from the user.
2.Get Category by Id - “/category/{category_id}”
It is a GET Request and takes Category UUID in category_id parameter as path variable
 */
@RestController
@RequestMapping("")
@CrossOrigin
public class CustomerController {
    /*This Method handles the Login request and takes authorization parameter in Base64 coded and produces a LoginResponse containing info customer
        and response header containing bearer accessToken. If error returns the error code with corresponding Message.
         */
    @RequestMapping(method = RequestMethod.POST, path = "/customer/login", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        /* Decoding the authorization token to get Access Token and UserName and Password */
        authFormatCheck(authorization);
        byte[] decode = Base64.getDecoder().decode(authorization.split(" ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");

        /* Getting Customer from Customer Auth Token if Customer Exists, setting header with access token and
         * sending login response
         *  */
        CustomerAuthEntity customerAuthTokenEntity = customerService.authenticate(decodedArray[0], decodedArray[1]);
        CustomerEntity customerEntity = customerAuthTokenEntity.getCustomer();
        LoginResponse loginResponse = new LoginResponse().id(customerEntity.getUuid()).message("LOGGED IN SUCCESSFULLY");
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", customerAuthTokenEntity.getAccessToken());
        return new ResponseEntity<LoginResponse>(loginResponse, headers, HttpStatus.OK);
    }

    /* This Method Performs check whether Authorization is in correct format or not - Called In Login*/
    public void authFormatCheck(final String authorization) throws AuthenticationFailedException {
        try {
            byte[] decoded = Base64.getDecoder().decode(authorization.split(" ")[1]);
            String decodedText = new String(decoded);
            String[] decodedArray = decodedText.split(":");
            if (authorization != null && authorization.startsWith("Basic ") && decodedArray.length == 2) {
                return;
            } else {
                throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
            }
        } catch (Exception e) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }
    }
    /* This method handles the customer logout ,It takes the Bearer accessToken from authorization in the header and logs out the customer
    and returns a LogoutResponse conatining UUID of customer and the successful message.If error returns the error code with corresponding Message.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/customer/logout", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {
        /* Decoding Authorization to get access token and username and password */
        String[] authorizationData = authorization.split(" ");
        String accessToken = authorizationData[1];
        /* Sending Access Token For Logging Out  */
        CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
        customerAuthEntity = customerService.logout(accessToken);
        CustomerEntity customer = customerAuthEntity.getCustomer();
        /* Sending Logout Response */
        LogoutResponse authorizedLogoutResponse = new LogoutResponse().id(customer.getUuid()).message("LOGGED OUT SUCCESSFULLY");
        return new ResponseEntity<LogoutResponse>(authorizedLogoutResponse,  HttpStatus.OK);
    }

    /* This method handles the customer details update request. Takes the request as UpdateCustomerRequest and produces UpdateCustomerResponse containing the details of the updated Customer.
        If error returns the error code with corresponding Message.
         */
    @RequestMapping(method = RequestMethod.PUT, path = "/customer", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> updateCustomer(@RequestHeader("authorization") final String authorization,
                                                                 @RequestBody(required = false) UpdateCustomerRequest updateCustomerRequest)
            throws AuthorizationFailedException, UpdateCustomerException {
        /* FirstName Field Should not be empty in  UpdateCustomerRequest*/
        if(updateCustomerRequest.getFirstName().isEmpty()) {
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }
        /* Decoding Authorization to get access token and username and password */
        String[] authorizationData = authorization.split("Bearer ");
        String userAccessToken = authorizationData[1];
        /* Sending Customer Entity For Updating Customer Entity Details in Database */
        CustomerEntity customerEntity = customerService.getCustomer(userAccessToken);

        customerEntity.setFirstName(updateCustomerRequest.getFirstName());
        customerEntity.setLastName(updateCustomerRequest.getLastName());

        CustomerEntity updateCustomer = customerService.updateCustomer(customerEntity);
        /* Sending UpdateCustomerResponse */
        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse().id(updateCustomer.getUuid()).firstName(updateCustomer.getFirstname()).lastName(updateCustomer.getLastname())
                .status("CUSTOMER DETAILS UPDATED SUCCESSFULLY");
        return new ResponseEntity<UpdateCustomerResponse>(updateCustomerResponse, HttpStatus.OK);
    }

     /* This method is to updateCustomerPassword the customer using oldPassword,newPassword & customerEntity and return the CustomerEntity .
     If error throws exception with error code and error message.
     */

    @RequestMapping(method = RequestMethod.PUT, path = "/customer/password", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdatePasswordResponse> updatePassword(@RequestHeader("authorization") final String authorization,
                                                                 @RequestBody(required = false) UpdatePasswordRequest updatePasswordRequest )
            throws AuthorizationFailedException, UpdateCustomerException{
        /* If Old or new password is empty, throw error */
        if(updatePasswordRequest.getNewPassword().isEmpty() || updatePasswordRequest.getOldPassword().isEmpty()) {
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }
        /* Decoding Authorization to get access token and USername and Password */
        String[] authorizationData = authorization.split("Bearer ");
        String userAccessToken = authorizationData[1];
        /* Sending Customer Entity To Update Password */
        CustomerEntity customerEntity = customerService.getCustomer(userAccessToken);

        String newPassword = updatePasswordRequest.getNewPassword();
        String oldPassword = updatePasswordRequest.getOldPassword();
        CustomerEntity updatedCustomerEntity = customerService.updateCustomerPassword(oldPassword, newPassword, customerEntity);
        /* Sending UpdatePasswordResponse */
        UpdatePasswordResponse updatePasswordResponse = new UpdatePasswordResponse().id(updatedCustomerEntity.getUuid())
                .status("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");
        return new ResponseEntity<UpdatePasswordResponse>(updatePasswordResponse, HttpStatus.OK);
    }
}
