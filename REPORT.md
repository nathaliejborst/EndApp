# CORVEE
## Nathalie Borst

Able to create groups with members and tasks. Every task has a startdate and frequency. The app divides group members into a schedule for every task and shows personal schedule with tasks from every group in the user’s personal calendar. 

### App structure


<img src="https://raw.githubusercontent.com/nathaliejborst/EndApp/master/doc/Screenshot_1517496877.png" width="160" height="265" />


#### LoginActivity.java
Let the user log into the app with e-mail and password or register with a non-unique username, e-mail and password. Also checks requirements like special characters for usernames Firebase can’t handle and checks for no blank fields.  Accounts are stored in Firebase. Re-directs the user if he/she is already logged in. User is created using the User class. 


#### MainActivity.java
Functions as a fragment container and initiates bottomnavigationbar and handles on click for bottombar items. Also shows user’s current name on top of screen. 

#### EasterActivity.java
Displays a gif. Is opened when logo is clicked on log in screen. 

<img src="https://raw.githubusercontent.com/nathaliejborst/EndApp/master/doc/profilefragment.png" width="160" height="265" />


#### ProfileFragment.java
User is able to log out and views it’s personal tasks. By clicking on the tasks button, tasks from every group the user is a member in will be retrieved from Firebase and shown in a custom listview (CalendarTaskAdapter.java). The user will be re-direct to the group details on list item click. 

<img src="https://raw.githubusercontent.com/nathaliejborst/EndApp/master/doc/showgroupsfragment.png" width="160" height="265" />


#### ShowGroupsFragment.java
Contains two buttons. One button let the user create a new group and re-directs to a new fragment. The other button shows the groups the user is a member of and the amount of tasks and users per group in a custom listview (ShowGroupsAdapter.java). When clicked on the group listview item, the user is re-directed to the concerning group details fragment.

<img src="https://raw.githubusercontent.com/nathaliejborst/EndApp/master/doc/groupnameactivity.png" width="160" height="265" />


#### GroupnameFragment.java
User can set a groupname and choose a group color for a new group he/she wants to create. Fragment checks groupname requirements and handles the visual aspect of selecting a color. Then it re-directs to find users fragment where he/she can add members to their newly added group.

<img src="https://raw.githubusercontent.com/nathaliejborst/EndApp/master/doc/findusersfragment.png" width="160" height="265" />


#### FindUsersFragment.java
User can search users by e-mail using the .startsAt() function from Firebase. Fragment shows name and e-mail of user from search results in a custom listview (GroupMembersAdapter.java) and is able to add the user to a users-to-add list. When clicking on a user in that list the user is removed from the users-to-add-list. Furthermore the user can create the group and thus add the group with it’s members and details to Firebase. When created, the user gets re-directed to the details of the newly created group.

<img src="https://raw.githubusercontent.com/nathaliejborst/EndApp/master/doc/groupdetailsfragment.png" width="160" height="265" />


#### GroupDetailsFragment.java
Shows groupname of given group on top of screen and sets a semi-transparant overlay of the groupcolor over the groupphoto. Retrieves users and tasks from group and fills custom listviews with the members (GroupMembersAdapter.java)  and tasks of groups (CalendarTaskAdapter.java). User is able to create a new task by filling in a taskname and on confirm gets re-directed to the add task fragment in order to choose a startdate, frequency and add the task to Firebase. Fragment also doesn’t allow empty tasknames or duplicate tasknames within a group. 

<img src="https://raw.githubusercontent.com/nathaliejborst/EndApp/master/doc/addtaskfragment.png" width="160" height="265" />


#### AddTaskFragment.java
Dialogfragment that implements the compact calendar library. Fragment receives taskname and groupdetails from previous fragment. User is able to choose a startdate for the task by clicking a date on the calendar. User is also able to choose between frequencies daily, weekly and monthly. When creating the task the users will randomly by arranged into a schedule. Task is added to every user’s child and the group child in Firebase. Task is created using the Task class.

<img src="https://raw.githubusercontent.com/nathaliejborst/EndApp/master/doc/calendarfragment.png" width="160" height="265" />


#### CalendarFragment.java
Uses the compact calendar library to show users tasks per date. Retrieves tasks for every group of user and makes a personal schedule given the task frequency, startdate amount of users and the group schedule. Creates an event for every task of user with the marker color as group color. Also shows tasks per day when day clicked in a custom listview. On list item click the groupname is shown via and alert message.


#### ShowGroupsAdapter.java
Custom listview adapter extendingArrayAdapter showing the groupcolor, groupname amount of tasks and amount of members of every group. Takes a list of Task objects as input.


#### GroupMembersAdapter.java
Custom listview adapter extending BaseAdapter showing a user’s screenname and e-mail address. Takes a list of User objects as input.


#### CalendarTaskAdapter.java
Custom listview adapter extending BaseAdapter showing the frequency, startdate and name of a task. The frequencies are stored as ints from 0 to 2 in Firebase. Adapter uses a list where the received frequency corresponds to a position of the list which contains the string value of the frequency. Takes a list of Task objects as input. 


#### User.class
Used to add and retrieve users from Firebase. User contains a username, e-mail and id and takes the username and e-mail as input. Function current user can be called to get id and username of current user. Function accesses Firebase.


#### Task.class
Used to add en retrieve tasks from Firebase. Task contains a taskname, startdate, groupid, groupname, frequency groupcolor and schedule. Schedule is an ordered arralist with user ID’s.


#### Group.class
Used to retrieve groupdetails from Firebase. Group contains a groupcolor, groupname, tasksamount and usersamount. Also a getter and setter for groupcolor are used to call the groupcolor in the ShowGroupsAdapter. 



### Process
I have deviated a little bit from my MVP along the way. My initial plan was to also implement switch requests with other members of the group and the ability to remove tasks and users from groups. This however turned out to be a lot of work I didn’t manage to complete in the end due to my Firebase structure. I also wanted to implement the Google calendar API which also turned out to be quit diffecult and also not necessary for the usage of my app. Instead of this API I used the Compact Calendar library which I used to visualize a user’s personal task schedule. Furthermore I had a lot of problems with my laptop because it is to slow to handle Android studio. After borrowing an Android phone the usage of the program improved, but I was able to use the macbook the last 1,5 week of the project which made it possible for me to actually complete my app and made big leaps of improvement compared to before. That’s why the amount of work I’ve done the past four weeks is skewed, because I got a lot more work done the last 1,5 week.



### Future improvements 
In the future I would make more use of my classes. Right now I mostly use my classes to add or retrieve ‘forms’ from Firebase, but access the database in every fragment and activity. Looking back, making functions in the classes which get the data from Firebase would’ve been a lot more efficient and organized. Right now I use bundles to send some data to the next class while I also could’ve made a function in my classes.
If I will continue improving my app, I will implement the switch requests and ability to change tasks and groups. I also would like to add more frequencies and also show the group schedule in the app instead of the user’s own schedule. 


