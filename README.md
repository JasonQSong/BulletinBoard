# BulletinBoard
OSU CSE 5461 Network Programming Project2

##AUTHOR

Jason Song.988

##FILES

All Java and Python file
Makefile
ReadMe

##How to run

For server, please run BulletinBoardServer.java

For Client, please run BulletinBoardClient.py

##How to use

There are following command to use.

1.Log in as username john

	%USERNAME John

2.Show all the groups can join

	%GROUPS

3.Join a group by its ID or its name

	%GROUPJOIN groupID/groupName

4.To post a message to a specific group,first select a group using “%GROUPPOST +groupID/Name”.Then use “MessageSubject:MessageContent” to implement your following post messages to this group.If you want to change group to post, again do “%GROUPPOST +groupID/Name”.

	%GROUPPOST groupID/groupName 
	Subject1:Content1
	Subject2:Content2
	Subject3:Content3

5.To get a specific message by its groupID and MessageID

	%GROUPMESSAGE GROUPID MessageID
	
6.To get a list of the users of a group

	%GROUPUSERS GroupID/GroupName

7.To leave a group

	%GROUPLEAVE GroupID/GroupName

8.To leave bulletinBoard

	%EXIT

For the server, we use multithreading to issue every client a single thread to avoid conflict.The sever get Json data from clients, parse the data and extract the command, get the data, put it back into Json and then send to the clients. Client gather user input from a simple GUI, put it into Json and send to the server.Client also receive Json data from server and parse it and show it on the GUI.

##Additional Credit Point

1.We use python for client side, Java for Server side

2.We implement a simple GUI for client


