# MuseumFinder
Developed during the Software Engineering course, Museum Finder is a Java desktop application for the **management and retrieval of information relating to a large number of Italian museums**.

## Prerequisites
Museum Finder depens on a relational database that contains information on the various museums (~7500), extracted from Wikipedia through a **web scraping** process.

## Users and functionalities
All users who want to use the services offered by Museum Finder must log in with their credentials.
The software is meant for three different types of users:

* The **administrator** is responsible for keeping the data correct and up-to-date: he receives error reports and after verifying their validity, makes changes to the database. 
Also, he takes care of adding new museums and removing those that no longer adhere to the service.

* Regular **users** have the ability to search among the museums in the database through a search bar, and then access the related information. The search function can be set according to various **criteria**, which determine the results: for example, it can be searched by _keywords_, by _distance_ from a certain location or by _positive reviews_. 
In addition, users can also book visits to a museum or leave reviews.

* Finally, **museum owners** can request the addition (but also the removal) of their own museum. The owner of a museum that is present in the database can access an interface that allows him to perform various operations: add events (ticket price reductions, special exhibitions) or view a series of statistics such as the number of visits to the page or the number of reservations.

The application also includes an error reporting mechanism; users can make reports if they notice inaccuracies or missing information for a given museum. These are reviewed by the respective museum owners who have the option to approve or disapprove them. The approved requests are then sent to the administrator, who proceeds with the modification. Any changes can also be requested directly from the owner of a museum and in this case obviously do not require approval.


https://user-images.githubusercontent.com/22869166/203126882-13af9aad-57c9-4fb1-bfea-db7edf972602.mov

## Design and implementation
Besides the functionalities the software provides, the aim of the project is to show that we are able to design and structure a JavaSE application.

In order to deliver the final project, various steps had to be completed:
* **requirements analysis**, with the definition of _functional_ and _structural_ requirements;
* **design**, with _class_ and _model_ diagrams, _use case_ tamplates and diagrams;
* **implementation**, using appropriate [ **design patterns**](#design-patterns);
* **unit testing**, using JUnit.

More information about this process can be found in the [project document](museumfinder.pdf) (in italian).

### Design pattern
The following design patterns have been used:
* **Gateway** (DAO)  
Dialogue with a database is an essential aspect of the project; controllers and other classes that are part of the application logic often need to receive specific information from the _data layer_. To hide the complexity of the latter from the rest of the application, we decided to use the **Data Access Object** (or **Table Data Gateway**, in Martin Fowler's lexicon) structural pattern. Each of the 5 Gateways encapsulates a database table (sometimes more than one if their context of use is the same) and contains in its methods all the SQL code necessary for the query.

* **Object pool**  
Another necessity, due to the dialogue with the database, was to maintain a reliable and always available connection with it. Opening a new connection every time there is a necessity is an expensive operation; on the other hand, keeping the same connection open, sharing it among the various Gateway classes is not a good idea. The task of the **ConnectionPool** class is precisely to create, manage and possibly close connections to the database. At startup, the program asks the ConnectionPool to open 5 connections; every time a Gateway needs to execute a query, it requests one and returns it after its use. The ConnectionPool, before providing a connection, tests if it is still valid and, if not, replaces it. At the end of the execution all the connections still open are closed.

* **Singleton**  
The Singleton pattern was necessary when we had to control access to a shared resource, in our case a file and the database. The first case study is the **Log** class, which initializes a logger with the appropriate settings and makes it available in the rest of the code: the use of a Singleton is even more legitimized since it allows to obtain the object instance in a safe way practically everywhere (and logging is, at the right level, always appropriate after every action). A similar argument can be made for Gateways, which relate to the database and whose single instance is provided to various classes through the use of a _Factory_.

* **Proxy**  
To memorize and operate with the list of museums that constitutes the result of a search, we have created a special _MuseumList_ class. Objects of this type take the query relating to the user's search as a parameter in their constructor and thus create the list of results. Since this list can be very long and is created at the same time as the containing object, we decided to use the Proxy pattern to implement **lazy initialization**. Through the proxy, in fact, we create the list of results only when this is really requested, i.e. when a search is performed. In addition to managing the lifecycle of MuseumList objects, this proxy allows us to do caching, returning the same instance of the list if the user requests the display of multiple results.

* **Strategy**  
The user may want to search for a museum by favoring different criteria: by ordering the results only by relevance or also by distance from a location or by feedback from other users. These different possibilities translate into different queries to be used to query the database and moreover one (and only one) of these strategies must be used to sort the search results. The behavioral pattern Strategy allows our program to change at any time (at the user's discretion), the algorithm that regulates the museum search. It takes the form of the **SearchStrategy** interface which is implemented by **ScoreStrategy**, **LocationStrategy** and **RatingStrategy**.

* **Builder**  
The main function of Museum Finder is the information display of a museum. These are of different types, and consequently the museum class has a large number of attributes (which could also increase in subsequent releases, as the information of interest increases). Creating a museum-type object would have required a constructor that would have taken many parameters; we also needed an additional constructors. The Builder pattern can help creating complex objects.  
The Builder we used is not the classic one of the _GoF_ (Gang of Four), but it is a private and static nested class within the Museum, which contains a series of setter methods, which, always returning the Builder instance, allow through method chaining to make the object creation code much more readable. This type of builder is used by _Joshua Bloch_ in his book _Effective Java_, in particular in _item 2_. In addition to the readability and greater clarity of the code, with the builder we avoid creating the so-called **telescopic constructors** and separate the object creation logic from any business logic.

* **Factory**  
For the creation of the various Gateways we used the **abstract factory** pattern. This allows us to group the gateway creation logic into one place in the code, making it easier to maintain. We also chose this pattern because it allows grouping the objects created into families, in our case we were interested in dividing the gateways based on the language supported by the database that is used to store the data. This subdivision makes modifications easier, since it is easy to change the concrete factory used, that is instantiated only once. The decision to use this pattern is partly due to the desire to keep the application open to extension: in fact, we have implemented only one type of factory because at the moment we have used a relational database that uses _postgreSQL_.

* **MVC**  
For our program we have created a graphical interface with java **Swing** and **Swingx**. Given this choice, we decided to adopt the **MVC** (model-view-controller) pattern, an architectural pattern for designing and structuring interactive applications. The application is divided into three parts (corresponding in the code to the packages of the same name): the **model**, which represents the data model of interest for the application, the **controller**, which defines the control logic and the application functions, the **view**, which manages the data presentation logic.  
Our MVC operates in a very simple way: the view represents the pages that are shown to the user and with which he can interact; for every user action a controller method is invoked, which in turn calls a method of the model (or more often of a gateway). When the operations (modification or data request) performed on the model are completed, the controller returns a value, or simply the control, to the view that invoked it.
