# auth-service

## API Auth Service

Authentication service.

## Configure email for password reset

Email from which the reset password email will be sent to the user

Go into
`application.properties`

Set

`username=${your-email-address@gmail.com}` (needs to be a gmail account)

`password=${password}`

After go to the following url

`https://myaccount.google.com/lesssecureapps?pli=1`

and set the property to true

## Run the app

Start up the database, type the following in a terminal

`docker-compose up`

Then

`mvn clean spring-boot:run`
