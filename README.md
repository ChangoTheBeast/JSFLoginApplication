# JSF Login Application
The goal of this project was to create a login application using JSF's
and Java EE.

## Table Of Contents:
1. [Requirements](#requirements)
2. [Login Screen](#login-screen)
    1. [Front End Components](#front-end-components)
    2. [Back End Components](#login-backend)
3. [Authentication](#authentication)
4. [Registration](#registration)
    1. 


## Requirements:
 - Login screen that takes a username and password.
 - Authentication of the login.
 - Both User and Admin roles.
 - Content locked behind particular roles.
 - Admins can register new users.
 - Good Styling.
 
## Login Screen
 
 ### Front End Components
 
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
 ### Front end Components
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
 ### Backend Components
<!--stackedit_data:
eyJoaXN0b3J5IjpbMTE2MTM5OTg2XX0=
-->