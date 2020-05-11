# qa-engineer-challenge

This project is intended to demonstrate my expertise in manual and automation testing, my capacity in developing 
Behavior Driven development (BDD) by writing Gherkin based features for test automation, as well as my quality oriented mindset
in analysing if the product under development is functional and reliable.

## Getting Started

### Download the project from repository

Using Git:

    git clone https://github.com/rapholtz/qa-engineer-challenge.git
    cd qa-engineer-challenge

Or simply downloading the [zip file](https://github.com/rapholtz/qa-engineer-challenge.git).

### Prerequisites

Java 1.8 or higher with the **JAVA_HOME** enviroment variable configured.

## Run the tests

Unzip the downloaded file in your computer.

Open a command line utility and navigate to the downloaded directory.

Tip: If you are using MacOS change the directory permissions using the following command:
    
    chmod +x mvnw

You will need to update the chromedriver executable path in the config.properties file.

Update the **webdriver.location** property with the path of executable downloaded from https://chromedriver.chromium.org/downloads

    ex: webdriver.location=//Users//myname//Documents//chromedriver

Type the command below and wait for the project to build and start running the tests.

    ./mvnw clean install test

## Built With

* [Java](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html) - The programming language used
* [Eclipse](https://www.eclipse.org/) - The Java IDE
* [Maven](https://maven.apache.org/) - Dependency Management
* [Cucumber](https://cucumber.io/) - Behavior Driven Development (BDD) framework
* [Selenium](https://www.selenium.dev/) - Browser automation framework
* [REST Assured](http://rest-assured.io/) - Testing and validating of REST services
