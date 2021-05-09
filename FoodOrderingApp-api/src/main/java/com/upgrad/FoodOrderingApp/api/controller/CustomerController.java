package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
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
}
