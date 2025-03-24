# quick-task
## Database layout in pgAdmin 4
_________
![Image alt](https://github.com/LestFeeD/quick-task/blob/main/Database.png)

## API endpoints
_________
## Swagger REST API docs:
http://localhost:8080/swagger-ui/index.html#/

Models
______
+ Status
+ StatusRole
+ Priority
+ Tag
+ Project
+ CommentProject
+ ProjectParticipants
+ StatusProject
+ Task
+ CommentTask
+ StatusTask
+ TaskParticipants
+ WebUser
+ ConfirmationToken

Configure  Application Properties
________
spring.jpa.hibernate.ddl-auto=validate

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOURUSERNAME
spring.mail.password=YOURPASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

spring.security.oauth2.client.registration.google.client-id =YOURID
spring.security.oauth2.client.registration.google.client-secret=YOURIDSECRET

auth.token.jwtSecret=YOURJWTSECRET
auth.token.expiration=YOURTIME
