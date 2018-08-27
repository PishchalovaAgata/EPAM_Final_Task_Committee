# EPAM_Final_Task_Committee
"University Committee" EPAM final task 

<h2>University Committee</h3>

<table style="table-layout: fixed; width:100%;">
  <tr>
    <td>
A web application for an university committee.

The application has two roles: admin and entrant.

Only registered users have access to all the functional of the application.

When client registered into the system he/she should confirm e-mail by clicking special link that will be sent to his/her e-mail, only after these actions user will be activated.

Authorisation can be successfull only for activated users.

After authorisation into system clients will see a list of faculties that entrants can be registered on(Entrant can be registered only on one faculty). 
Admin has special feature he can filter this list by order and subjects and also can delete or update Faculty Info. Admin cannot delete faculty or update list of subjects, if there are entrants that are registered on it.
Another feature is that admin can see Report Sheets of the Faculties, where he can find information about enlisted entrants, when the Enrollment of Faculty isn't over, and submitted or cancelled entrants, when it's over.

When entrant doesn't visit Personal Cabinet and doesn't fill information about his marks, he will be shown all faculties.
If he register information about him as entrant(Certificate, Marks), he will see filtered list by his marks' subjects.

What concerning Personal Cabinet, there admin and entrant can update their User Info.
Entrant can add or update Entrant Info and if entrant is registered on faculty, can get Faculty Info and Entrant Status("ENLISTED", "SUBMITTED", "CANCELED"), also Entrant is able to change his registration on faculty. 
Admin in Personal Cabinet has an opportunity to change list of subjects in system(Add, Update, Delete) and can add new Faculties. 

After changing Entrant Status on SUBMITTED or CANCELLED entrant becomes unavailable.
</td>
    <td style="width:50%;"><h4>Technical description:</h4>

Java 8

JavaEE: Servlet

Server / Servlet container: Tomcat 8.5.31

Database: MySQL, JDBC

Logger: Log4J

Tests: TestNG

Front Part: React</td>
  </tr>
</table>
