/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hatta.consumer;


import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Hatta NR
 */
public class App {

    public static final String AUTH_SERVER_URI = "http://localhost:8081/WebApp/oauth/token";

    public static final String QPM_PASSWORD_GRANT = "?grant_type=password&username=tes&password=testes";

    public static final String QPM_ACCESS_TOKEN = "?access_token=";

    public static final String REST_SERVICE_URI = "http://localhost:8081/WebApp";

//Preparing HTTP HEADERS
    private static HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;
    }
    
    /*
     * Add HTTP Authorization header, using Basic-Authentication to send client-credentials.
     */
    private static HttpHeaders getHeadersWithClientCredentials(){
        String plainClientCredentials="frontend:secret";
        String base64ClientCredentials;
        base64ClientCredentials = new String(Base64.encodeBase64(plainClientCredentials.getBytes()));
         
        HttpHeaders headers = getHeaders();
        headers.add("Authorization", "Basic " + base64ClientCredentials);
        return headers;
    }    
    
    /*
     * Send a POST request [on /oauth/token] to get an access-token, which will then be send with each request.
     */
    @SuppressWarnings({ "unchecked"})
    private static AuthTokenInfo sendTokenRequest(){
        RestTemplate restTemplate = new RestTemplate(); 
         
        HttpEntity<String> request = new HttpEntity<String>(getHeadersWithClientCredentials());
        ResponseEntity<Object> response = restTemplate.exchange(AUTH_SERVER_URI+QPM_PASSWORD_GRANT, HttpMethod.POST, request, Object.class);
        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>)response.getBody();
        AuthTokenInfo tokenInfo = null;
         
        if(map!=null){
            tokenInfo = new AuthTokenInfo();
            tokenInfo.setAccess_token((String)map.get("access_token"));
            tokenInfo.setToken_type((String)map.get("token_type"));
            tokenInfo.setRefresh_token((String)map.get("refresh_token"));
            tokenInfo.setExpires_in((int)map.get("expires_in"));
            tokenInfo.setScope((String)map.get("scope"));
            System.out.println(tokenInfo);
            System.out.println("access_token ="+map.get("access_token")+", token_type="+map.get("token_type")+", refresh_token="+map.get("refresh_token")
            +", expires_in="+map.get("expires_in")+", scope="+map.get("scope"));;
        }else{
            System.out.println("No user exist----------");
             
        }
        return tokenInfo;
    }
    
    
    /* GET */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void listAllCustomers(AuthTokenInfo tokenInfo) {
        
        Assert.notNull(tokenInfo, "Authenticate first please......");
        
        RestTemplate restTemplate = new RestTemplate();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
           
        HttpEntity<String> request = new HttpEntity<String>(getHeaders());        
        ResponseEntity<List> response = restTemplate.exchange(REST_SERVICE_URI+"/api/user"+QPM_ACCESS_TOKEN+tokenInfo.getAccess_token(),
                HttpMethod.GET, request, List.class);
        List<LinkedHashMap<String, Object>> customerMap = (List<LinkedHashMap<String, Object>>)response.getBody();
//         
//        List<LinkedHashMap<String, Object>> customerMap = restTemplate.getForObject(REST_SERVICE_URI + "/api/user", List.class);

        if (customerMap != null) {
            System.out.println("Retrieve all customers:");
            for (LinkedHashMap<String, Object> map : customerMap) {
                System.out.println("Customer : id=" + map.get("id") + ", Name=" + map.get("name")
                        + ", Address=" + map.get("email") + ", Email=" + map.get("password") + map.get("enabled"));
            }
        } else {
            System.out.println("No customer exist----------");
        }
    }

    private static void createCustomer(User user,AuthTokenInfo tokenInfo) {
        Assert.notNull(tokenInfo, "Authenticate first please......");
        RestTemplate restTemplate = new RestTemplate();
        System.out.println("Create a customer: " + user.getName());
        HttpEntity<Object> request = new HttpEntity<Object>(user, getHeaders());
        restTemplate.postForLocation(REST_SERVICE_URI + "/api/user"+QPM_ACCESS_TOKEN+tokenInfo.getAccess_token(), request, User.class);
    }

    private static void updateCustomer(User user,AuthTokenInfo tokenInfo) {
         Assert.notNull(tokenInfo, "Authenticate first please......");
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Object> request = new HttpEntity<Object>(user, getHeaders());
        System.out.println("Update a customer: " + user.getId());
        restTemplate.put(REST_SERVICE_URI + "/api/user"+QPM_ACCESS_TOKEN+tokenInfo.getAccess_token(),request);
        
    }

    private static User getCustomer(int id,AuthTokenInfo tokenInfo) {
        Assert.notNull(tokenInfo, "Authenticate first please......");
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<String>(getHeaders());
        User cust = restTemplate.getForObject(REST_SERVICE_URI + "/api/user/" + id+QPM_ACCESS_TOKEN+tokenInfo.getAccess_token(), User.class);
        if (cust != null) {
            System.out.println("Retrieve a customer:");
            System.out.println("Customer : id=" + cust.getId() + ", Name=" + cust.getName()
                    + ", Address=" + cust.getPassword() + ", Email=" + cust.getEmail());
            return cust;
        } else {
            System.out.println("No customer exist----------");
            return null;
        }
    }

    /* DELETE */
    private static void deleteCustomer(int id,AuthTokenInfo tokenInfo) {
        Assert.notNull(tokenInfo, "Authenticate first please......");
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<String>(getHeaders());
        System.out.println("Delete a customer: " + id);

        restTemplate.delete(REST_SERVICE_URI + "/api/user/" + id+QPM_ACCESS_TOKEN+tokenInfo.getAccess_token(),request);
    }

    /* DELETE */
    private static void deleteAllCustomers(AuthTokenInfo tokenInfo) {
        Assert.notNull(tokenInfo, "Authenticate first please......");
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<String>(getHeaders());
        System.out.println("Delete all customers");
        restTemplate.delete(REST_SERVICE_URI + "/api/user"+QPM_ACCESS_TOKEN+tokenInfo.getAccess_token(),request);
    }

    public static void main(String[] args) {
        AuthTokenInfo tokenInfo = sendTokenRequest();
        listAllCustomers(tokenInfo);
        
        deleteCustomer(26, tokenInfo);
      //  User user = getCustomer(26, tokenInfo);
        //user.setName("susi");
        //user.setPassword("admin");
//      
////        user.setPassword("password");
       // updateCustomer(user, tokenInfo);
//          User newUser = new User();
//          newUser.setId(2);
//          newUser.setName("hatta");
//          newUser.setEmail("hatta@susi");
//          newUser.setPassword("hatta");
////        
//        createCustomer(newUser, tokenInfo);
        
    }
}
