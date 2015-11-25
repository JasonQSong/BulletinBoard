# BulletinBoard
OSU CSE 5461 Project2

ReadMe for BulletinBoard
———————————————————————————————————————————
author: Wei Pan  pan.520
	Yue Cao  cao.613

———————————————————————————————————————————
Included:
	All Java and Python file
	Makefile
	ReadMe

How to run:
For server, please run BulletinBoardServer.java
For Client, please run BulletinBoardClient.py

How to use:
There are following command to use.
1.%USERNAME John
  Log in as username john
2.%GROUPS
  Show all the groups can join
3.%GROUPJOIN groupID/groupName
  Join a group by its ID or its name
4.%GROUPPOST groupID/groupName 
  Subject1:Content1
  Subject2:Content2
  Subject3:Content3
  To post a message to a specific group,first select a group using “%GROUPPOST +groupID/Name”.Then use “MessageSubject:MessageContent” to implement your following post messages to this group.If you want to change group to post, again do “%GROUPPOST +groupID/Name”.
5.%GROUPMESSAGE GROUPID MessageID
  To get a specific message by its groupID and MessageID
6.%GROUPUSERS GroupID/GroupName
  To get a list of the users of a group
7.%GROUPLEAVE GroupID/GroupName
  To leave a group
8.%EXIT
  To leave bulletinBoard

For the server, we use multithreading to issue every client a single thread to avoid conflict.The sever get Json data from clients, parse the data and extract the command, get the data, put it back into Json and then send to the clients. Client gather user input from a simple GUI, put it into Json and send to the server.Client also receive Json data from server and parse it and show it on the GUI.

Additional Credit Point:
1.We use python for client side, Java for Server side
2.We implement a simple GUI for client


