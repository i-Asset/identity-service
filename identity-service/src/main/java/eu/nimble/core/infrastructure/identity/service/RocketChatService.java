//package eu.nimble.core.infrastructure.identity.service;
//
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import eu.nimble.core.infrastructure.identity.constants.GlobalConstants;
//import eu.nimble.core.infrastructure.identity.entity.dto.Credentials;
//import eu.nimble.core.infrastructure.identity.entity.dto.FrontEndUser;
//import eu.nimble.core.infrastructure.identity.system.IdentityController;
//import eu.nimble.core.infrastructure.identity.system.dto.rocketchat.channel.CreateChannelRequest;
//import eu.nimble.core.infrastructure.identity.system.dto.rocketchat.channel.CreateChannelResponse;
//import eu.nimble.core.infrastructure.identity.system.dto.rocketchat.list.ChatUser;
//import eu.nimble.core.infrastructure.identity.system.dto.rocketchat.list.ChatUsers;
//import eu.nimble.core.infrastructure.identity.system.dto.rocketchat.list.UserEmail;
//import eu.nimble.core.infrastructure.identity.system.dto.rocketchat.login.RocketChatLoginResponse;
//import eu.nimble.core.infrastructure.identity.system.dto.rocketchat.register.RockerChatRegisterResponse;
//import org.json.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.HttpStatusCodeException;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by Nirojan Selvanathan on 28.05.18.
// */
//@Service
//public class RocketChatService {
//
//    private static final Logger logger = LoggerFactory.getLogger(IdentityController.class);
//
//    @Value("${nimble.rocketChat.url}")
//    private String rocketChatURL;
//
//    @Value("${nimble.rocketChat.user}")
//    private String rocketChatUser;
//
//    @Value("${nimble.rocketChat.password}")
//    private String rocketChatPassword;
//
//    @Value("${nimble.rocketChat.chatServiceURL}")
//    private String chatServiceURL;
//
//    public CreateChannelRequest createChannel(CreateChannelRequest createChannelRequest, List<String> members) {
//
//        String uri = rocketChatURL + "/api/v1/channels.create";
//
//        JSONObject request = new JSONObject();
//        request.put("name", createChannelRequest.getChannelName());
//        request.put("members", members);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("X-User-Id", createChannelRequest.getUserId());
//        headers.set("X-Auth-Token", createChannelRequest.getUserToken());
//        HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
//
//        RestTemplate rs = new RestTemplate();
//
//        try {
//            ResponseEntity<String> response = rs.exchange(uri, HttpMethod.POST, entity, String.class);
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//            CreateChannelResponse createChannelResponse = new CreateChannelResponse();
//
//            createChannelResponse = mapper.readValue(response.getBody(), CreateChannelResponse.class);
//            // handle channel name conflict
//            if (!createChannelResponse.isSuccess()) {
//
//            } else {
//                persistChannelName(createChannelRequest);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return createChannelRequest;
//    }
//
//    /**
//     * @param createChannelRequest
//     * @return
//     */
//    public String checkIfChannelExist(CreateChannelRequest createChannelRequest) {
//
//        RestTemplate rs = new RestTemplate();
//        String uri = chatServiceURL + "/channels";
//
//        JSONObject request = new JSONObject();
//        request.put("catalogueID", createChannelRequest.getProductName());
//        request.put("initiatingPartyID", createChannelRequest.getInitiatingPartyID());
//        request.put("respondingPartyID", createChannelRequest.getRespondingPartyID());
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
//
//        try {
//            ResponseEntity<String> registerResponse = rs.exchange(uri, HttpMethod.POST, entity, String.class);
//            if (registerResponse.getStatusCode().value() == 200) {
//                ObjectMapper mapper = new ObjectMapper();
//                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//                CreateChannelRequest rq = mapper.readValue(registerResponse.getBody(), CreateChannelRequest.class);
//                logger.info("Channel name : {} found in chat service", rq.getChannelName());
//                return rq.getChannelName();
//            } else {
//                logger.info("Existing channel not found for: {}", createChannelRequest.toString());
//                return null;
//            }
//        } catch (Exception e) {
//            logger.error("Error when obtaining channel data from chat service: {}", createChannelRequest.toString());
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    /**
//     * @param createChannelRequest
//     * @return
//     */
//    public boolean persistChannelName(CreateChannelRequest createChannelRequest) {
//
//        RestTemplate rs = new RestTemplate();
//        String uri = chatServiceURL + "/channel";
//
//        JSONObject request = new JSONObject();
//        request.put("catalogueID", createChannelRequest.getProductName());
//        request.put("initiatingPartyID", createChannelRequest.getInitiatingPartyID());
//        request.put("respondingPartyID", createChannelRequest.getRespondingPartyID());
//        request.put("channelName", createChannelRequest.getChannelName());
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
//
//        try {
//            ResponseEntity<String> registerResponse = rs.exchange(uri, HttpMethod.POST, entity, String.class);
//            if (registerResponse.getStatusCode().value() == 201) {
//                logger.info("Channel name : {} persisted in chat service", createChannelRequest.getChannelName());
//                return true;
//            } else {
//                logger.info("Error when persisting channel : {} , name in chat service", createChannelRequest.getChannelName());
//                return false;
//            }
//        } catch (Exception e) {
//            logger.info("Error when persisting channel : {} , name in chat service", createChannelRequest.getChannelName());
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//
//    /**
//     * This function will return the missing users in Rocket.Chat in a Map
//     *
//     * @param chatUsers
//     * @return
//     */
//    public Map<String, String> getMissingEmails(ChatUsers chatUsers, List<String> initiatingParty, List<String> respondingList) {
//        Map<String, String> emailUsernameMap = new HashMap<>();
//
//
//        for (String m : initiatingParty) {
//            for (ChatUser c : chatUsers.getUsers()) {
//                if (null != c.getEmails()) {
//                    for (UserEmail e : c.getEmails()) {
//                        if (e.getAddress().equals(m)) {
//                            emailUsernameMap.put(e.getAddress(), c.getUsername());
//                        }
//                    }
//                }
//            }
//        }
//
//        for (String m : respondingList) {
//            for (ChatUser c : chatUsers.getUsers()) {
//                if (null != c.getEmails()) {
//                    for (UserEmail e : c.getEmails()) {
//                        if (e.getAddress().equals(m)) {
//                            emailUsernameMap.put(e.getAddress(), c.getUsername());
//                        }
//                    }
//
//                }
//            }
//        }
//
//        for (String m : initiatingParty) {
//            emailUsernameMap.putIfAbsent(m, null);
//        }
//        for (String m : respondingList) {
//            emailUsernameMap.putIfAbsent(m, null);
//        }
//
//        return emailUsernameMap;
//    }
//
//    public ChatUsers listUsers() {
//        Credentials credentials = new Credentials();
//        credentials.setUsername(rocketChatUser);
//        credentials.setPassword(rocketChatPassword);
//        RocketChatLoginResponse rocketChatLoginResponse = loginOrCreateUser(new FrontEndUser(), credentials, false, false);
//        return listUsers(rocketChatLoginResponse.getData().getAuthToken(), rocketChatLoginResponse.getData().getUserId());
//    }
//
//    /**
//     * This function uses the admin users credentials to obtain user list as normal users credentials will only fetch user name
//     *
//     * @param authToken
//     * @param userID
//     * @return
//     */
//    public ChatUsers listUsers(String authToken, String userID) {
//
//        ChatUsers chatUsers = new ChatUsers();
//        String uri = rocketChatURL + "/api/v1/users.list";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("X-User-Id", userID);
//        headers.set("X-Auth-Token", authToken);
//        HttpEntity<String> entity = new HttpEntity<String>(headers);
//
//        RestTemplate rs = new RestTemplate();
//        try {
//
//            ResponseEntity<String> response = rs.exchange(uri, HttpMethod.GET, entity, String.class);
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//            try {
//                chatUsers = mapper.readValue(response.getBody(), ChatUsers.class);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return chatUsers;
//    }
//
//    /**
//     * The following method registers a user in Rocket.Chat, User name will be created firstname.lastname.email_domain
//     *
//     * @param frontEndUser
//     * @param credentials
//     * @param userNameAlreadyExists
//     * @return
//     */
//    public RockerChatRegisterResponse registerUser(FrontEndUser frontEndUser, Credentials credentials, boolean userNameAlreadyExists, int retryCounter) {
//
//        RockerChatRegisterResponse rockerChatRegisterResponse = new RockerChatRegisterResponse();
//
//        try {
//            RestTemplate rs = new RestTemplate();
//            String uri = rocketChatURL + "/api/v1/users.register";
//
//            String temp = credentials.getUsername().substring(credentials.getUsername().indexOf("@") + 1); // e.g. @google.com
//            String companyEmailName = temp.substring(0, temp.indexOf("."));
//
//            String rocketChatUserName = frontEndUser.getFirstname() + "." + frontEndUser.getLastname() + "." + companyEmailName;
//            if (userNameAlreadyExists) {
//                rocketChatUserName = rocketChatUserName + ((int) (Math.random() * 100));
//            }
//
//            JSONObject request = new JSONObject();
//            request.put(GlobalConstants.EMAIL_STRING, credentials.getUsername());
//            request.put("pass", new Md5PasswordEncoder().encodePassword(credentials.getUsername(), null).substring(0, 8));
//            request.put(GlobalConstants.USER_NAME_STRING, rocketChatUserName);
//            request.put("name", frontEndUser.getFirstname() + " " + frontEndUser.getLastname());
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
//
//
//            ResponseEntity<String> registerResponse = rs.exchange(uri, HttpMethod.POST, entity, String.class);
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//            rockerChatRegisterResponse = new RockerChatRegisterResponse();
//            rockerChatRegisterResponse = mapper.readValue(registerResponse.getBody(), RockerChatRegisterResponse.class);
//
//            if (rockerChatRegisterResponse.isSuccess() && null == rockerChatRegisterResponse.getError()) {
//                logger.info("A new user have been created in Rocket.Chat with username {}", rockerChatRegisterResponse.getUser().getUsername());
//            }
//
//            // If the user name already exists then a new user name should be created
//            if (rockerChatRegisterResponse.isSuccess() && null != rockerChatRegisterResponse.getError()) {
//                if (rockerChatRegisterResponse.getError().equals("Username is already in use") && retryCounter <= 1) {
//                    logger.info("User name conflict when creating a new user with username {}", rocketChatUserName);
//                    rockerChatRegisterResponse = registerUser(frontEndUser, credentials, true, retryCounter + 1);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return rockerChatRegisterResponse;
//    }
//
//    /**
//     * This function tries to login a user to Rocket.Chat, if the user is not present in Rocket.Chat then it will create
//     * a new user in Rocket.Chat. This behaviour has been added to support existing users in the platform
//     *
//     * @param frontEndUser
//     * @param credentials
//     * @return
//     */
//    public RocketChatLoginResponse loginOrCreateUser(FrontEndUser frontEndUser, Credentials credentials, boolean createIfMissing, boolean generatePass) {
//
//        RocketChatLoginResponse rocketChatLoginResponse = new RocketChatLoginResponse();
//
//        try {
//            RestTemplate rs = new RestTemplate();
//            String uri = rocketChatURL + "/api/v1/login";
//
//            JSONObject request = new JSONObject();
//            request.put(GlobalConstants.EMAIL_STRING, credentials.getUsername());
//            if (generatePass) {
//                request.put("password", new Md5PasswordEncoder().encodePassword(credentials.getUsername(), null).substring(0, 8));
//            } else {
//                request.put("password", credentials.getPassword());
//            }
//
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
//
//            ResponseEntity<String> loginResponse = rs.exchange(uri, HttpMethod.POST, entity, String.class);
//            rocketChatLoginResponse = mapper.readValue(loginResponse.getBody(), RocketChatLoginResponse.class);
//
//        } catch (HttpStatusCodeException exception) {
//            if (exception.getStatusCode().value() == 401 && createIfMissing) {
//                RockerChatRegisterResponse rockerChatRegisterResponse = registerUser(frontEndUser, credentials, false, 0);
//                if (rockerChatRegisterResponse.isSuccess()) {
//                    rocketChatLoginResponse = loginOrCreateUser(frontEndUser, credentials, false, true);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return rocketChatLoginResponse;
//    }
//}
