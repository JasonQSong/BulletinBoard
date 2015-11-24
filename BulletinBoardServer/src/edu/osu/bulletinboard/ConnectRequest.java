package edu.osu.bulletinboard;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;



final class ConnectRequest implements Runnable {
	final static String CRLF = "\r\n";
	Socket socket;

	// Constructor
	public ConnectRequest(Socket socket) throws Exception {
		this.socket = socket;
	}

	// Implement the run() method of the Runnable interface.
	public void run() {
		try {
			processRequest();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void processRequest() throws Exception {
		// Get a reference to the socket's input and output streams.
		InputStream is = socket.getInputStream();
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		// print welcome lines and asks for username
		os.writeBytes("Welcome to Bulletin!!!\n");
		os.writeBytes("Please enter your username!\n");

		// Get the request line of the HTTP request message.
		String username = br.readLine();
		int userID = AddUser(username);

		// read group selection using join command
		String GroupName = null;
		int GroupID = 0;

		// print user guide
		os.writeBytes("-----------------------USER GUIDE-------------------------\n");
		os.writeBytes("If you want to goin a group, use %GROUPJOIN+group name or group#\n");
		os.writeBytes("If you want to post a message, use %GROUPPOST+group name/ID+subject+content\n");
		os.writeBytes("If you want to retrieve a message, use %GET+Message ID\n");
		os.writeBytes("If you want to %leave, use LEAVE\n");
		os.writeBytes("----------------------------------------------------------\n");

		// print last 2 message
		PostLastTwoMessage();

		String s = br.readLine();
		while (true) {// infinite loop until user leave

			if (s != null) {// if command read

				StringTokenizer tokens = new StringTokenizer(s);
				String CommandType = tokens.nextToken();// get command type

				// check if command type is right
				if (DataList.CheckCommandType.contains(CommandType)) {

					// if GROUPJOIN
					if (CommandType.equals("%GROUPJOIN")) {

						String groupString = GetGroupString(s);

						if (DataList.GroupList.contains(groupString)) {
							GroupName = groupString;
							GroupID = DataList.GroupList.indexOf(groupString);
							os.writeBytes("You join group " + GroupID + " "
									+ GroupName + "!\n");
							DataList.GroupSelectionList[userID][GroupID] = true;
						} else {
							try {
								GroupID = Integer.parseInt(groupString);
								GroupName = DataList.GroupList.get(GroupID);
								os.writeBytes("You join group " + GroupID + " "
										+ GroupName + "!\n");
								DataList.GroupSelectionList[userID][GroupID] = true;
							} catch (Exception e) {
								os.writeBytes("Unrecognized group selection! Please try again!\n");
							}
						}
					}

					// if GROUPPOST
					if (CommandType.equals("%GROUPPOST")) {
						try {
							// Get group
							String GroupString = tokens.nextToken();
							String MessageGroupName;
							int MessageGroupID;

							Object[] Sarray = new Object[3];
							Sarray = GetGroupNameAndID(GroupString);
							// if there is error getting group name and ID
							if (Sarray[2].equals("ERROR")) {
								Exception e = new Exception();
								throw e;
							} else {
								MessageGroupName = (String) Sarray[0];
								MessageGroupID = (int) Sarray[1];

								// if user is not in the target group he can't
								// post to that group
								if (DataList.GroupSelectionList[userID][MessageGroupID] == false) {
									Exception e = new Exception();
									throw e;
								}
							}

							// Get Subject
							// Subject should only be a word without space here
							String SubjectString = tokens.nextToken();

							// Get Content
							String ContentString = "";
							while (tokens.hasMoreTokens()) {
								ContentString = ContentString
										+ tokens.nextToken() + " ";
							}
							// ContentString.substring(0,ContentString.length()
							// - 1);

							if (!ContentString.equals("")) {
								AddandPostMessage(userID, username,
										MessageGroupID, SubjectString,
										ContentString);
							}
						} catch (Exception e) {
							os.writeBytes("Incorrect Command! Please try again!\n");
						}

					}

					// if GROUPUSERS
					if (CommandType.equals("%GROUPUSERS")) {
						try {
							// Get group information
							String GroupString = tokens.nextToken();
							String GroupName1;
							int GroupID1;

							Object[] Sarray = new Object[3];
							Sarray = GetGroupNameAndID(GroupString);
							// if there is error getting group name and ID
							if (Sarray[2].equals("ERROR")) {
								Exception e = new Exception();
								throw e;
							} else {
								GroupName1 = (String) Sarray[0];
								GroupID1 = (int) Sarray[1];

								os.writeBytes("----------------------------------------------------------\n");
								os.writeBytes("Users in group " + GroupName1
										+ " are:\n");
								for (int i = 0; i < DataList.UserNum; i++) {
									User user = DataList.UserList.get(i);
									if (DataList.GroupSelectionList[user.ID][GroupID1] == true) {
										os.writeBytes(CRLF);
										os.writeBytes("User ID: " + user.ID
												+ "\n");
										os.writeBytes("User Name: " + user.Name
												+ "\n");
									}
								}
								os.writeBytes("----------------------------------------------------------\n");
							}
						} catch (Exception e) {
							os.writeBytes("Incorrect Command! Please try again!\n");
						}
					}

					// if GROUPLEAVE
					if (CommandType.equals("%GROUPLEAVE")) {
						try {
							// Get group
							String GroupString = tokens.nextToken();
							String GroupName1;
							int GroupID1;

							Object[] Sarray = new Object[3];
							Sarray = GetGroupNameAndID(GroupString);
							// if there is error getting group name and ID
							if (Sarray[2].equals("ERROR")) {
								Exception e = new Exception();
								throw e;
							} else {
								GroupName1 = (String) Sarray[0];
								GroupID1 = (int) Sarray[1];
								SendLeaveNotification(username, GroupID1);
								DeleteUser(userID);
								os.writeBytes("Successfully leave group "
										+ GroupID1 + "!!\n");
							}
						} catch (Exception e) {

						}
					}
					
					//IF EXIT
					if (CommandType.equals("%EXIT")) {
						break;
					}
					// if GROUPMESSAGE
					if (CommandType.equals("%GROUPMESSAGE")) {
						tokens.nextToken();
						int GetMessageID = Integer.parseInt(tokens.nextToken());
						Message GetMessage = DataList.MessageList
								.get(GetMessageID);
						os.writeBytes("----------------------------------------------------------\n");
						os.writeBytes("Message ID: " + GetMessage.ID + "\n");
						os.writeBytes("Message sender: " + GetMessage.sender
								+ "\n");
						os.writeBytes("Message Post Date: "
								+ GetMessage.Postdate + "\n");
						os.writeBytes("Message Subject: " + GetMessage.Suject
								+ "\n");
						os.writeBytes("Message Content: " + GetMessage.Content
								+ "\n");
						os.writeBytes("----------------------------------------------------------\n");
					}
					
					//if GROUP
					if (CommandType.equals("%GROUPS")) {
						// print select groups
						os.writeBytes("----------------------------------------------------------\n");
						os.writeBytes("Groups you can join:\n");
						for (int i = 0; i < DataList.GroupList.size(); i++) {
							os.writeBytes(i + " " + DataList.GroupList.get(i) + "\n");
						}
						os.writeBytes("----------------------------------------------------------\n");
					}
				} else {
					os.writeBytes("Undefined Command!!\n");
				}
			}

			s = br.readLine();
		}// end while true
		os.close();

	}

	private void SendLeaveNotification(String username, int groupID) {

		String groupname = DataList.GroupList.get(groupID);
		for (int i = 0; i < DataList.UserNum; i++) {
			User user = DataList.UserList.get(i);
			if ((DataList.GroupSelectionList[user.ID][groupID] == true)
					&& (!user.Name.equals(username))) {
				try {
					Socket clientSocket = user.Socket;
					DataOutputStream os1 = new DataOutputStream(
							clientSocket.getOutputStream());
					os1.writeBytes("----------------------------------------------------------\n");
					os1.writeBytes("User " + username + " left group "
							+ groupname + "!\n");
					os1.writeBytes("----------------------------------------------------------\n");
					// os1.close();
				} catch (Exception e) {
				}
			}
		}

	}

	private int AddUser(String username) {
		DataList.UserNum++;
		DataList.UserMaxID++;
		User user = new User();
		user.Name = username;
		user.ID = DataList.UserMaxID;
		user.Socket = socket;
		DataList.UserList.add(user.ID, user);

		return DataList.UserNum - 1;
	}

	private void DeleteUser(int userID) {
		DataList.UserList.remove(userID);
		DataList.UserNum--;
	}

	private void AddandPostMessage(int userID, String username, int groupID,
			String subject, String content) {
		DataList.MessageNum++;
		Message message = new Message();
		message.Content = content;
		message.Suject = subject;
		message.ID = DataList.MessageNum - 1;
		message.sender = username;
		message.senderID = userID;
		message.Postdate = new Date();
		DataList.MessageList.add(message.ID, message);

		for (int i = 0; i < DataList.UserNum; i++) {
			User user = DataList.UserList.get(i);
			if (DataList.GroupSelectionList[user.ID][groupID] == true) {
				try {
					Socket clientSocket = user.Socket;
					DataOutputStream os1 = new DataOutputStream(
							clientSocket.getOutputStream());
					os1.writeBytes("----------------------------------------------------------\n");
					os1.writeBytes("Message ID: " + message.ID + "\n");
					os1.writeBytes("Message sender: " + message.sender + "\n");
					os1.writeBytes("Message group: " + groupID + "\n");
					os1.writeBytes("Message Post Date: " + message.Postdate
							+ "\n");
					os1.writeBytes("Message Subject: " + message.Suject + "\n");
					os1.writeBytes("----------------------------------------------------------\n");
					// os1.close();
				} catch (Exception e) {
				}
			}
		}

	}

	private String GetPostContent(String s) {
		int startIndex;
		startIndex = s.indexOf("T");
		if (startIndex == s.length() - 1)
			return "";
		startIndex = startIndex + 2;
		if (startIndex > (s.length() - 1))
			startIndex = s.length() - 1;
		return s.substring(startIndex, s.length());
	}

	private String GetGroupString(String s) {
		int startIndex;
		startIndex = s.indexOf("N");
		if (startIndex == s.length() - 1)
			return "";
		startIndex = startIndex + 2;
		if (startIndex > (s.length() - 1))
			startIndex = s.length() - 1;
		return s.substring(startIndex, s.length());
	}

	private void PostLastTwoMessage() {
		try {
			DataOutputStream os1 = new DataOutputStream(
					socket.getOutputStream());
			if (DataList.MessageNum >= 2) {
				Message messageItem = DataList.MessageList
						.get(DataList.MessageNum - 2);
				os1.writeBytes("Message ID: " + messageItem.ID + "\n");
				os1.writeBytes("Message sender: " + messageItem.sender + "\n");
				os1.writeBytes("Message Post Date: " + messageItem.Postdate
						+ "\n");
				os1.writeBytes("Message Subject: " + messageItem.Content + "\n");
				os1.writeBytes("----------------------------------------------------------\n");
			}
			if (DataList.MessageNum >= 1) {
				Message messageItem = DataList.MessageList
						.get(DataList.MessageNum - 1);
				os1.writeBytes("Message ID: " + messageItem.ID + "\n");
				os1.writeBytes("Message sender: " + messageItem.sender + "\n");
				os1.writeBytes("Message Post Date: " + messageItem.Postdate
						+ "\n");
				os1.writeBytes("Message Subject: " + messageItem.Content + "\n");
				os1.writeBytes("----------------------------------------------------------\n");
			}

		} catch (Exception e) {
		}
	}

	private Object[] GetGroupNameAndID(String groupstring) {
		Object[] ans = new Object[3];// ans[0]=groupname,ans[1]=groupID,ans[2]
										// is a flag
		ans[2] = "";

		if (DataList.GroupList.contains(groupstring)) {
			ans[0] = groupstring;
			ans[1] = DataList.GroupList.indexOf(groupstring);
		} else {
			try {
				ans[1] = Integer.parseInt(groupstring);
				ans[0] = DataList.GroupList.get((int) ans[1]);
			} catch (Exception e) {
				ans[2] = "ERROR";
			}
		}
		return ans;
	}
}