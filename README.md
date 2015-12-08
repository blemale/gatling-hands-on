# Gatling Hands On

This hands on will guide you through most of Gatling HTTP features.
You’ll learn about simulations, scenarios, feeders, recorder, loops, etc.

We will test an application named Computer-Database deployed at the URL: http://computer-database.gatling.io.
This application is a simple CRUD application for managing computer models.

This hands on is of fork of  [Gatling Quickstart](http://gatling.io/docs/2.1.7/quickstart.html) and
[Gatling Advanced Tutorial](http://gatling.io/docs/2.1.7/advanced_tutorial.html).

## Prerequisites

* Java 7 or higher
* Maven
* Eclipse + [ScalaIDE](http://scala-ide.org/) or IntelliJ + Scala Plugin
* Firefox

## Step 0: Set up

* Reset project: `> git reset --hard step-0`
* Import project in IDE, as a standard maven project

## Step 1: Recorder

### Using the recorder

To ease the creation of the scenario, we will use the Recorder, 
a tool provided with Gatling that allows you to record your actions on a web application and export them as a Gatling scenario.

You can launch this tool using the `Recorder` class.

Set it up with the following options:
* com.github.blemale.computerdatabase package
* BasicSimulation name
* Follow Redirects? checked
* Automatic Referers? checked
* Infer html resources? checked
* Remove conditional cache headers? checked
* Black list first filter strategy selected
* `.*\.css`, `.*\.js` and `.*\.ico` in the black list filters

After configuring the recorder,
all you have to do is to start it and configure your browser to use Gatling Recorder’s proxy.

In the Recorder, you have to define one port (for both HTTP and HTTPS): the local proxy port, by default 8000.
This is the port your browser must connect to so that the Recorder is able to capture your navigation.

Then, you have to configure your browser to use the defined port.
Here is how to do with Firefox, open the browser Advanced settings,
then go to the Network panel and update the connection settings with:
* Manual proxy configuration: checked
* HTTP Proxy: 127.0.0.1
* Port: 8000
* Use this proxy server for all protocol: checked

### Recording the scenario

Now simply start recording and browse the application:
* Enter ‘Search’ tag.
* Go to the website: [http://computer-database.gatling.io](http://computer-database.gatling.io)
* Search for models with ‘macbook’ in their name.
* Select ‘Macbook pro’.
* Enter ‘Browse’ tag.
* Go back to home page.
* Iterates several times through the model pages by clicking on Next button.
* Enter ‘Edit’ tag.
* Click on Add new computer.
* Fill the form.
* Click on Create this computer.

Try to act as a real user would, don’t immediately jump from one page to another without taking the time to read.
This will make your scenario closer to real users’ behavior.

When you have finished playing the scenario, click on Stop in the Recorder interface.

The Simulation will be generated under the name BasicSimulation.scala.
You can take a look at it, if you don't understand everything, it is normal.
We will explain the simulation structure soon.

### Running Gatling

To run Gatling simulation, you have to launch the `Engine` class and follow instruction on the console.

### Gatling scenario explained

First execute `> git reset --hard step-1-solution` and open the `BasicSimulation.scala` file.
The simulation structure is explained thanks to comments.

### Going further

From now for each step you have to:
* `> git reset --hard step-i`
* Replace `???` in `BasicSimulation.scala` class according to instructions

You can checkout solution of a given step with `> git reset --hard step-i-solution`.

## Step 2: PageObject

Presently our Simulation is one big monolithic scenario.

So first let us split it into composable business processes, akin to the PageObject pattern with Selenium.
This way, you’ll be able to easily reuse some parts and build complex behaviors without sacrificing maintenance.

In our scenario we have three separated processes:
* Search: search models by name
* Browse: browse the list of models
* Edit: edit a given model

You are going to extract those chains and store them into objects. Objects are native Scala singletons.
Moreover you are going to give proper names instead of generated ones, because they appear on reports.

## Step 3: Injection

So, this is great, we can load test our server with... one user! Let’s increase the number of users.

First let’s define two populations of users:
* regular users: they can search and browse computer models.
* admin users: they can search, browse and also edit computer models.

Then you are going to increase the number of simulated users, try to inject 10 regular users.
Don't inject to many load ecause we don’t want to flood our test web application.
Please, be kind and don’t crash our server ;-)

If you want to simulate 3000 users, you might not want them to start at the same time.
Indeed, real users are more likely to connect to your web application gradually.

In our scenario let’s have 10 regular users and 2 admins, and ramp them over 10 seconds so we don’t hammer the server.

To do that take a look at [Injection reference page](http://gatling.io/docs/2.1.7/general/simulation_setup.html#injection).

## Step 4: Feeders and Checks

We have set our simulation to run a bunch of users, but they all search for the same model.
Wouldn’t it be nice if every user could search a different model name?

We need dynamic data so that all users don’t play exactly the same scenario and we end up with a behavior completely different from the live system (due to caching, JIT etc.).
This is where Feeders will be useful.

Feeders are data sources containing all the values you want to use in your scenarios.
There are several types of Feeders, the most simple being the CSV Feeder: this is the one we will use in our test.

First let’s create a file named search.csv and place it in the `user-files/data` folder.

This file contains the following lines:

    searchCriterion,searchComputerName
    Macbook,MacBook Pro
    eee,ASUS Eee PC 1005PE

Then let’s declare a feeder and use it to feed our users with the above data.

To do that take a look at [Expression EL reference page](http://gatling.io/docs/2.1.7/session/expression_el.html),
[Feeder reference page](http://gatling.io/docs/2.1.7/session/feeder.html#feeder)
and [Check reference page](http://gatling.io/docs/2.1.7/http/http_check.html#http-check).

## Step 5: Loops

In the browse process we have a lot of repetition when iterating through the pages.
We have four times the same request with a different query param value.
Can we change this to not violate the DRY principle?

First we will extract the repeated `exec` block to a function.
Indeed, Simulation‘s are plain Scala classes so we can use all the power of the language if needed.
We can now call this function and pass the desired page number.

But we still have repetition, let's try to refactor `browse` with a looping structure.

To do that take a look at [Loops reference page](http://gatling.io/docs/2.1.7/general/scenario.html#scenario-loops).

## Step 6: Checks and failure management

Up until now we have only used `check` to extract some data from the html response and store it in the session.
But check is also handy to check properties of the response.
By default Gatling checks if the http response status is 20x or 304.

First to demonstrate failure management we will introduce a check on a condition that fails randomly.

Then let's try to handle this random failure by trying at most two time the random failing block and by exiting the whole scenario if no attempt succeed.

To do that take a look at [Error management reference page](http://gatling.io/docs/2.1.7/general/scenario.html#error-management).

That's all Folks!
