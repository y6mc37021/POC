The code has a small UI with few basic functionalites and also the backend JUnit test cases have been written to test the api/functionalites. 

ChatServiceController Methods

/connect
When user get connected this api would be called. User will select the room from the list of available rooms or can create a new room and clicks on connect by entring his name. Room name is unique to its session. 
Connect Request :- Should have "user name" and "room name". 
Note :- To keep it simple there is no option from UI to selecte room. It is been consided as part of back JUnit test case. 

/sendMessage
When user sends a messgase the same would be broadcasted to all the users.

/userTyping
This api will be used to send typing event to the to all the connected users. 

/leaveRoom 
If a users gets disconnected the same will be broadcasted to all the users.

How to Run :
ChatServiceApplication --> Run as Java Applicaiton 
Once the Server is Up

Browser - http:/localhost:8080
or 
The below test classes could be used to run individually 
ReceiveMessageWhenTyping
RecieveNotificationIfLeft
SendRecieveMessagesTest









