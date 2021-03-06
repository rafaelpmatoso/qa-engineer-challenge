# qa-engineer-challenge

This project is intended to demonstrate my expertise in manual and automation testing, my capacity in developing 
Behavior Driven development (BDD) by writing Gherkin based features for test automation, as well as my quality oriented mindset
in analysing if the product under development is functional and reliable.

I tried to develop the project in *Cypress* ([link to the repository](https://github.com/rapholtz/qa-engineer-challenge-cypress.git)) because it offers a complete testing environment that speeds up the automation process. But the site kept throwing uncaught exceptions and seeing as Cypress is an e2e testing tool, these errors couldn't be ignored.

So I decided to use something I'm more familiar with, Java and Selenium.

Suspicious bugs and improvement suggestions are registered in the [bugs.json](https://github.com/rapholtz/qa-engineer-challenge/blob/master/bugs/bug.json) file.

## Getting Started

### Download the project from repository

Using Git:

    git clone https://github.com/rapholtz/qa-engineer-challenge.git
    cd qa-engineer-challenge

Or simply downloading the [zip file](https://github.com/rapholtz/qa-engineer-challenge.git).

### Prerequisites

Java 1.8 or higher with the **JAVA_HOME** enviroment variable configured.

To run the tests a maven wrapper placed inside the project folder can be used, just follow the steps below.

## Run the tests

Unzip the downloaded file in your computer.

Open a command line utility and navigate to the downloaded directory.

Tip: If you are using MacOS change the directory permissions using the following command:
    
    chmod +x mvnw

You will need to update the chromedriver executable path in the **config.properties** file.

Update the **webdriver.location** property with the path of executable downloaded from https://chromedriver.chromium.org/downloads

**IMPORTANT**: *The chromedriver should be in the same version of the Google Chrome installed in the machine.*

    ex: webdriver.location=//Users//myname//Documents//chromedriver

Type the command below and wait for the project to build and start running the tests.

    ./mvnw clean install test

## Built With

* [Java (jdk 1.8)](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html) - The programming language used
* [Eclipse](https://www.eclipse.org/) - The Java IDE
* [Maven](https://maven.apache.org/) - Dependency Management
* [Cucumber](https://cucumber.io/) - Behavior Driven Development (BDD) framework
* [Selenium](https://www.selenium.dev/) - Browser automation framework
* [REST Assured](http://rest-assured.io/) - Testing and validating of REST services
