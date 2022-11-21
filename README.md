# MuseumFinder
Developed during the Software Engineering course, Museum Finder is a Java desktop application for the **management and retrieval of information relating to a large number of Italian museums**.

## Prerequisites
Museum Finder depens on a relational database that contains information on the various museums (~5000), extracted from Wikipedia through a **web scraping** process.

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

