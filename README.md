**Important: Don't forget to update the [Candidate README](#candidate-readme) section**

Real-time Transaction Challenge
===============================
## Overview
Welcome to Current's take-home technical assessment for backend engineers! We appreciate you taking the time to complete this, and we're excited to see what you come up with.

Today, you will be building a small but critical component of Current's core banking enging: real-time balance calculation through [event-sourcing](https://martinfowler.com/eaaDev/EventSourcing.html).

## Schema
The [included service.yml](service.yml) is the OpenAPI 3.0 schema to a service we would like you to create and host. 

## Details
The service accepts two types of transactions:
1) Loads: Add money to a user (credit)

2) Authorizations: Conditionally remove money from a user (debit)

Every load or authorization PUT should return the updated balance following the transaction. Authorization declines should be saved, even if they do not impact balance calculation.

You may use any technologies to support the service. We do not expect you to use a persistent store (you can you in-memory object), but you can if you want. We should be able to bootstrap your project locally to test.

## Expectations
We are looking for attention in the following areas:
1) Do you accept all requests supported by the schema, in the format described?

2) Do your responses conform to the prescribed schema?

3) Does the authorizations endpoint work as documented in the schema?

4) Do you have unit and integrations test on the functionality?

# Candidate README
## Bootstrap instructions
1. Prerequisites:
    -Java JDK11 or higher
    -Maven 3.6 or newer
    -An IDE of your choice (Intellij was used for building the app)
    -Git (for version control)

2. Source Code Acquisition : First you need to clone the repository from github to your local machine:
    Git clone https://github.com/yourusername/PROJECTNAME.git 
    Replace the http link with the project link
   
3. Project Setup :
      Through Intellij:
          -Open the Intellij idea IDE, select file>open and navigate to the project folder. Intellij should detect maven configuration itself. Build and run the project
      Through Command terminal:
          -Open the terminal at project directory
          -Use commands:
            1. mvn clean install
            2. mvn springboot:run

4. Use Postman to send requests to the service. Make sure the service port and port used in postman request are the same.

## Design considerations
1. Architecture: I chose a Microservice architecture to ensure the project was scalable and robust because the system needs to be designed to serve a large set of customers with high reliability.
2. Choice of Technology:
    -Spring Boot : It was chosen for its vast ecosystem and support for building production grade microservices. Springboot makes it easy to manage the services.
    -Concurrent Data Structures: Concurrent Hashmaps are used to ensure data integrity and performance in Multi-threaded environments typical of a real time transaction app.
    -Event Sourcing: Implemented to capture all changes to an application's state as a sequence of events, which can be queried and used to reconstruct past states. This provides a         reliable audit trail and the ability to undo or replay transactions if necessary. New snapshots are created at every 100 events which allows us to maintain a recent backup of our      events which can be crucial for quick recovery. 
3. Testing: A comprehensive set of unit test cases and integration tests have been covered to ensure the reliability of the app.
4. Performance: In-memory objects are used to keep track of events which makes the recovery of events quicker. A persistent database can be used as well to ensure a better backup is stored on the machine.

##Notable design considerations:
1. User creation: Since we did not have a frontend and no other way to create users. A new user is created as soon as we send the first “load” request to our service(UserId, currency,   balance). Before the first Load request, authorization request (for a new user) will be declined because the user does not exist in the system. 
2. Currency Consistency: The application mandates currency consistency for transactions. If a user attempts to add money in a different currency than what was previously used for the    user, the transaction will be declined.
3. Transaction Limit: The service allows a maximum transaction limit of 10,000 units for “Authorization” in a single request. Debit transactions exceeding this limit are automatically   declined. However, load transactions(credit) are allowed for any amount. This has been done to avoid any fraudulent transactions.
4. Transaction Type Restrictions: The load endpoint exclusively accepts "credit" in the "creditordebit" variable, while authorization is limited to "debit” only.
5. Value Restrictions: The system does not allow negative values for either credit or debit transactions.
6. Amount value restrictions: The amount value has to have a number. It can not be an empty string or null. Although, we accept comma separated values. If the amount has trailing or     leading whitespaces, it would be accepted. No whitespaces in between the numbers are allowed.



## Bonus: Deployment considerations

Below is a structured approach, i would suggest for deployment for this project:
1. Deployment Strategy: It is essential to adopt a strategy that supports continuous integration and deployment (CI/CD), allowing for rapid iterations and minimal downtime. This will   make the app more reliable and robust.
2. Hosting Environment: A secure, scalable and highly available hosting environment should be used. For example: Amazon AWS
    Amazon Web Services: It is known for its extensive services. Some of the key components that can be used are:
      EC2: to manage and scale the web servers and application servers
      RDS: Relational database with good database performance and scalability.
      S3: For storing logs
3. Elastic Load Balancing: To spread out the incoming traffic
    Other services like Microsoft Azure and Google Cloud Platform can also be used instead of AWS.
   
4. Containerization: Utilizing Docker for containerization of the application can allow consistent deployment regardless of the environment. 
5. CI/CD pipeline: can be facilitated through tools like Jenkins, GitLab CI, or GitHub Actions, enabling automated testing and deployment processes. 
6. Security Measures: All traffic should be encrypted using SSL/TLS to secure data in transit. Data encryption and network security needs to be configured properly to ensure a safe       transaction and avoid any fraudulent transactions.
7. Recovery: Regular backups and snapshots of the database and filesystems to ensure data can be restored to a known good state. 

## ASCII art
Charizard
                 ."-,.__
                 `.     `.  ,
              .--'  .._,'"-' `.
             .    .'         `'
             `.   /          ,'
               `  '--.   ,-"'
                `"`   |  \
                   -. \, |
                    `--Y.'      ___.
                         \     L._, \
               _.,        `.   <  <\                _
             ,' '           `, `.   | \            ( `
          ../, `.            `  |    .\`.           \ \_
         ,' ,..  .           _.,'    ||\l            )  '".
        , ,'   \           ,'.-.`-._,'  |           .  _._`.
      ,' /      \ \        `' ' `--/   | \          / /   ..\
    .'  /        \ .         |\__ - _ ,'` `        / /     `.`.
    |  '          ..         `-...-"  |  `-'      / /        . `.
    | /           |L__           |    |          / /          `. `.
   , /            .   .          |    |         / /             ` `
  / /          ,. ,`._ `-_       |    |  _   ,-' /               ` \
 / .           \"`_/. `-_ \_,.  ,'    +-' `-'  _,        ..,-.    \`.
.  '         .-f    ,'   `    '.       \__.---'     _   .'   '     \ \
' /          `.'    l     .' /          \..      ,_|/   `.  ,'`     L`
|'      _.-""` `.    \ _,'  `            \ `.___`.'"`-.  , |   |    | \
||    ,'      `. `.   '       _,...._        `  |    `/ '  |   '     .|
||  ,'          `. ;.,.---' ,'       `.   `.. `-'  .-' /_ .'    ;_   ||
|| '              V      / /           `   | `   ,'   ,' '.    !  `. ||
||/            _,-------7 '              . |  `-'    l         /    `||
. |          ,' .-   ,' ||               | .-.        `.      .'     ||
 `'        ,'    `".'    |               |    `.        '. -.'       `'
          /      ,'      |               |,'    \-.._,.'/'
          .     /        .               .       \    .''
        .`.    |         `.             /         :_,'.'
          \ `...\   _     ,'-.        .'         /_.-'
           `-.__ `,  `'   .  _.>----''.  _  __  /
                .'        /"'          |  "'   '_
               /_|.-'\ ,".             '.'`__'-( \
                 / ,"'"\,'               `/  `-.|" 


## License

At CodeScreen, we strongly value the integrity and privacy of our assessments. As a result, this repository is under exclusive copyright, which means you **do not** have permission to share your solution to this test publicly (i.e., inside a public GitHub/GitLab repo, on Reddit, etc.). <br>

## Submitting your solution

Please push your changes to the `main branch` of this repository. You can push one or more commits. <br>

Once you are finished with the task, please click the `Submit Solution` link on <a href="https://app.codescreen.com/candidate/4227d305-dea8-4118-8dd6-0bd103734d8e" target="_blank">this screen</a>.
