# eindproject

![Alt text](https://raw.githubusercontent.com/nathaliejborst/eindproject/master/doc/proposaleindproject.png)

##Corvee
###Nathalie Borst

This app helps users create a schedule for certain tasks within a group. This could be tasks like cleaning the kitchen, doing groceries, feeding the fishes, etc. In the app you can create an account and be part of a group. Subsequently you can create a task within a group and set the begin-, and optionally end-, date and the frequency of the task (weekly, monthly, every 3 days). The app schedules the user in a schedule and sends the user which turn it is to do the task a notification on the day itself or a day in advance. If the user has done the taks it can mark the task as done. If a user is somehow not able to do the task then he/she can make a switch-request. Any other user can accept that request and the app will switch the two users in the schedule. 

###Datasources

A shared calender API that is used within each group and maybe another fun API like random facts about dates or a image generator. 


###External components
  
Firebase and/or a Google log-in API to keep track of users, groups and tasks.


###Similar apps

There are some mobile apps that have similar features but are not the same. There are multiple to do-list apps where you can share tasks and there are also shared calendar apps where users can share a calendar. However this app is about a task that has to be repeated frequently within one group. This app makes a schedule with information as the groupmember and the frequency of the task. It also gives you the option to switch tasks if you’re not able to fulfill the task when it’s your turn.


###Hardest parts

Difficulties that can arise are implementing a shared calendar within a group. Creating a schedule and especially adjusting a schedule when the frequency or group members change. Also implementing a switch request will be challenging. 

