# JSF Login Application
The goal of this project was to create a login application using JSF's
and Java EE.

## Table Of Contents:
1. [Requirements](#requirements)
2. [Login Screen](#login-screen)
    1. [Frontend Components](#login-frontend)
    2. [Backend Components](#login-backend)
3. [Authentication](#authentication)
4. [Registration](#registration)
    1. [Frontend Components](#registration-frontend)
    2. [Backend Components](#registration-backend)
5. [Demo](#demo)
    1. [Login Screen](#login-screen)
    2. [Welcome Screen](#welcome-screen)
    3. [Cake Screen](#cake-screen)
    4. [Registration Screen](#registration-screen)
    5. [Forbidden Screen](#forbidden-screen)

## Requirements:
 - Login screen that takes a username and password.
 - Authentication of the login.
 - Both User and Admin roles.
 - Content locked behind particular roles.
 - Admins can register new users.
 - Good Styling.
 
## Login Screen
 
 ### Frontend Components <a name="login-frontend"></a>
 
  - Contains a form with two fields:
    - A username field
    - A password field
  - A submit button which sends the information in the form to the authenticator.
  
  #### JSF code
  ```xhtml
    <h:form id = "login">
                    <h:messages id="messages" globalOnly="true"/>
                    <h:panelGrid columns="2" cellspacing="5px" styleClass="login-form">
                        <h:outputLabel value="Username: "/>
                        <h:inputText id="username" value="#{loginBean.user.username}" styleClass="form-control"/>
                        <h:outputLabel value="Password: "/>
                        <h:inputSecret id="password" value="#{loginBean.user.password}" styleClass="form-control"/>
                    </h:panelGrid>
                    <h:commandButton styleClass="btn btn-primary btn-lg" action="#{loginBean.submit}" value="Submit"/>
                </h:form>  
```
### Backend Components <a name="login-backend"></a>
 - A bean to carry the information from the form to the authenticator
 - Redirect based on the authentication.
 - Logout method
 
 #### Login Bean code
  - Submit method:
    - ```java
       public void submit() throws IOException {
               switch (continueAuthentication()) {
                   case SEND_CONTINUE:
                       facesContext.responseComplete();
                       break;
                   case SEND_FAILURE:
                       facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "login unsuccessful", null));
                       break;
                   case SUCCESS:
                       externalContext.redirect(externalContext.getRequestContextPath() + "/view/welcome.xhtml");
               }
           }
      ```
  - Logout method:
    - ```java
      public String logout() throws ServletException {
              ((HttpServletRequest)facesContext.getExternalContext().getRequest()).logout();
              FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
              return "/login.xhtml?faces-redirect=true?";
          }
      ```
  ## Authentication
  - Takes in a username and a password
  - Compares it to the users in the database.
    - Checks if there is a user with the same username in the database
    - Hashes the password and compares to the hashed password in the database for that user
  - Sends the authentication back to the Login Bean
  
  #### Authentication Code
  - Continue Authentication in Login Bean:
    - ```java
      private AuthenticationStatus continueAuthentication() {
              return securityContext.authenticate(
                      (HttpServletRequest) externalContext.getRequest(),
                      (HttpServletResponse) externalContext.getResponse(),
                      AuthenticationParameters.withParams().credential(new UsernamePasswordCredential(user.getUsername(), user.getPassword()))
              );
          }
      ```
  - Admin authorisation:
    - ```java
      UsernamePasswordCredential usernamePasswordCredential = (UsernamePasswordCredential) credential;
              for (UsersEntity admin: new UsersDAO().getUsersByRole("admin")) {
                  try {
                      String password = Hashing.getHash(admin, usernamePasswordCredential.getPasswordAsString());
                      if (usernamePasswordCredential.getCaller().equals(admin.getUsername())
                              && password.equals(admin.getPassword())) {
                          HashSet<String> roles = new HashSet<>();
                          roles.add("ADMIN");
                          return new CredentialValidationResult(admin.getUsername(), roles);
                      }
                  } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                      return CredentialValidationResult.NOT_VALIDATED_RESULT;
                  }
              }
      ```
  - Hashing Implementation:
    - ```java
      public static String getHash(UsersEntity user, String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
              UserSecurityDAO securityDAO = new UserSecurityDAO();
              UserSecurityEntity userSecurity = securityDAO.getUserSecurityByID(user.getUserId());
              int its = userSecurity.getIterations();
              byte[] salt = userSecurity.getSalt();
              KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, its, 128);
              SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
              byte[] hashedPassword = factory.generateSecret(keySpec).getEncoded();
              return Base64.getEncoder().encodeToString(hashedPassword);
          }
      ```
## Registration
 ### Frontend Components <a name="registration-frontend"></a>
 - Contains a form with the following fields:
    - Username
    - Password
    - Role (selected via dropdown)
 - Submit button that sends the information to the registration bean.
 #### JSF Code
 ```xhtml
<h:form id = "registration">
    <h:messages id="messages" globalOnly="true"/>
    <h:panelGrid columns="2" cellspacing="5px" styleClass="login-form">
        <h:outputLabel value="Username: "/>
        <h:inputText id="username" value="#{userRegistrationBean.user.username}" styleClass="form-control"/>
        <h:outputLabel value="Password: "/>
        <h:inputSecret id="password" value="#{userRegistrationBean.user.password}" styleClass="form-control"/>
        <h:outputLabel value="Role: "/>
        <h:selectOneMenu value="#{userRegistrationBean.role}" styleClass="form-control">
            <f:selectItem itemValue="admin" itemLabel="Admin" />
            <f:selectItem itemValue="user" itemLabel="User" />
        </h:selectOneMenu>
    </h:panelGrid>
        <h:commandButton styleClass="btn btn-primary btn-lg" action="#{userRegistrationBean.submit}" value="Submit"/>
        <h:commandButton styleClass="btn btn-primary btn-lg" action="welcome?faces-redirect=true" value="Return"/>
</h:form>
 ```
 ### Backend Components <a name="registration-backend"></a>
 - Registration bean:
    - Takes in the form information.
    - Checks if user already exists.
    - Persists user to the database.
        - Generates a hashed password.
 #### Registration code
 ```java
public void submit() {
    List<UsersEntity> userList = usersDAO.getUserByName(user.getUsername());
    if (userList.size() > 0) {
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "User already exists in the database.", null));
    } else {
        try {
            HashMap<UserSecurityEntity, byte[]> securityEntityHashMap = Hashing.setHash(user.getPassword());
            String password = null;
            for (byte[] hashedPassword : securityEntityHashMap.values()) {
                password = Base64.getEncoder().encodeToString(hashedPassword);
            }

            UsersEntity userEntity = new UsersEntity();
            userEntity.setUsername(user.getUsername());
            userEntity.setPassword(password);
            userEntity.setRole(this.role);
            usersDAO.createUser(userEntity);
            userEntity = usersDAO.getUserByName(user.getUsername()).get(0);
            for (UserSecurityEntity entity : securityEntityHashMap.keySet()) {
                entity.setUserId(userEntity.getUserId());
                new UserSecurityDAO().createUserSecurity(entity);
            }
            facesContext.addMessage(null, new FacesMessage("User was successfully added.", null));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "User was unable to be added.", null));
        }
    }
}
 ```

## Demo
### Login Screen
![LoginScreenImage](/images/LoginScreen.png)
### Welcome Screen
![WelcomeScreenImage](/images/WelcomeScreen.png)
### Cake Screen
![CakeScreenImage](/images/CakeScreen.png)
### Registration Screen
![RegistrationScreenImage](/images/RegistrationScreen.png)
### Forbidden Screen
![ForbiddenAccessScreenImage](/images/ForbiddenScreen.png)
### Database
#### Users Table
![UsersTableImage](/images/UsersTable.png)
#### User Security Table
![UserSecurityTableImage](/images/UserSecurityTable.png)