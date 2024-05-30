
Real-time Transaction App
===============================
## Overview
Built a small component of a banking engine : Real time balance calculation using event-sourcing.
(https://martinfowler.com/eaaDev/EventSourcing.html).

## Schema
The [included service.yml](service.yml) is the OpenAPI 3.0 schema to a service used to create and host. 

## Details
The service accepts two types of transactions:
1) Loads: Add money to a user (credit)

2) Authorizations: Conditionally remove money from a user (debit)

Every load or authorization PUT should return the updated balance following the transaction. Authorization declines should be saved, even if they do not impact balance calculation.




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

## Notable design considerations:
1. User creation: Since we did not have a frontend and no other way to create users. A new user is created as soon as we send the first “load” request to our service(UserId, currency,   balance). Before the first Load request, authorization request (for a new user) will be declined because the user does not exist in the system. 
2. Currency Consistency: The application mandates currency consistency for transactions. If a user attempts to add money in a different currency than what was previously used for the    user, the transaction will be declined.
3. Transaction Limit: The service allows a maximum transaction limit of 10,000 units for “Authorization” in a single request. Debit transactions exceeding this limit are automatically   declined. However, load transactions(credit) are allowed for any amount. This has been done to avoid any fraudulent transactions.
4. Transaction Type Restrictions: The load endpoint exclusively accepts "credit" in the "creditordebit" variable, while authorization is limited to "debit” only.
5. Value Restrictions: The system does not allow negative values for either credit or debit transactions.
6. Amount value restrictions: The amount value has to have a number. It can not be an empty string or null. Although, we accept comma separated values. If the amount has trailing or     leading whitespaces, it would be accepted. No whitespaces in between the numbers are allowed.



## Deployment considerations

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

