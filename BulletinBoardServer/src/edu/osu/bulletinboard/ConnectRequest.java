package edu.osu.bulletinboard;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import java.util.StringTokenizer;


import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;


final class ConnectRequest implements Runnable {
	final static String CRLF = "\r\n";
	final static JSONParser JsonParser=new JSONParser();
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

	private String GetJsonString(BufferedReader br) {
		String ans = "";

		try {
			while (br.ready()) {
				ans = ans + br.readLine();

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ans;
	}

	private void processRequest() throws Exception {
		// Get a reference to the socket's input and output streams.
		InputStream is = socket.getInputStream();
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		// read group selection using join command
		String GroupName = null, username = null;
		int GroupID = 0;
		int userID = 0;
		/*
		 * // Get the request line of the HTTP request message. String username
		 * = br.readLine(); int userID = AddUser(username);
		 * 
		 * 
		 * 
		 * 
		 * // print last 2 message add to join PostLastTwoMessage();
		 */

		// From InputStream get JsonObject
		String InputString=null, s = null;
		if (br.ready())
		{
			InputString = GetJsonString(br);
			JSONObject jsonObject =(JSONObject)JsonParser.parse(InputString);
			s = (String)jsonObject.get("cmd");
		}
		

		while (true) {// infinite loop until user leave

			if (s != null) {// if command read

				StringTokenizer tokens = new StringTokenizer(s);
				String CommandType = tokens.nextToken();// get command type

				// check if command type is right
				if (DataList.CheckCommandType.contains(CommandType)) {

					// if username
					if (CommandType.equals("%USERNAME")) {
						username = tokens.nextToken();
						userID = AddUser(username);
					}

					// if GROUPJOIN
					if (CommandType.equals("%GROUPJOIN")) {

						String groupString = GetGroupString(s);

						if (DataList.GroupList.contains(groupString)) {
							GroupName = groupString;
							GroupID = DataList.GroupList.indexOf(groupString);
							// os.writeBytes("You join group " + GroupID + " "
							// + GroupName + "!\n");
							try {
								DataList.GroupSelection.get(userID)
										.add(GroupID);
							} catch (Exception eg) {
								ArrayList<Integer> arrayList = new ArrayList<Integer>();
								arrayList.add(GroupID);
								DataList.GroupSelection.add(arrayList);
							}
							// DataList.GroupSelectionList[userID][GroupID] =
							// true;
						} else {
							try {
								GroupID = Integer.parseInt(groupString);
								GroupName = DataList.GroupList.get(GroupID);
								// os.writeBytes("You join group " + GroupID +
								// " "
								// + GroupName + "!\n");
								// DataList.GroupSelectionList[userID][GroupID]
								// = true;
								try {
									DataList.GroupSelection.get(userID).add(
											GroupID);
								} catch (Exception eg) {
									ArrayList<Integer> arrayList = new ArrayList<Integer>();
									arrayList.add(GroupID);
									DataList.GroupSelection.add(arrayList);
								}
							} catch (Exception e) {// new Group
								try {// groupstring is groupID
									GroupID = Integer.parseInt(groupString);
									GroupName = "UnamedNewGroup";
									DataList.GroupList.add("UnamedNewGroup");
									DataList.GroupIDList.add(GroupID);
									DataList.GroupSelection.get(userID).add(
											GroupID);
								} catch (Exception e1) {// groupstring is
														// groupname
									GroupName = groupString;
									GroupID = DataList.GroupList.size();
									DataList.GroupList.add(groupString);
									DataList.GroupIDList.add(GroupID);
									DataList.GroupSelection.get(userID).add(
											GroupID);
								}
								// os.writeBytes("Unrecognized group selection! Please try again!\n");
							}
						}

						// send notification
						SendNotification(username, GroupID, "JOIN");
					}

					// if GROUPPOST
					if (CommandType.equals("%GROUPPOST")) {
						try {
							// Get group
							String GroupString = tokens.nextToken();
							//String MessageGroupName;
							int MessageGroupID;

							Object[] Sarray = new Object[3];
							Sarray = GetGroupNameAndID(GroupString);
							// if there is error getting group name and ID
							if (Sarray[2].equals("ERROR")) {
								Exception e = new Exception();
								throw e;
							} else {
								//MessageGroupName = (String) Sarray[0];
								MessageGroupID = (int) Sarray[1];

								// if user is not in the target group he can't
								// post to that group
								if (CheckInGroup(userID, MessageGroupID) == false) {
									Exception e = new Exception();
									throw e;
								}
							}

							String SubjectString = null, ContentString = null;
							try {
								// Get Subject
								// Subject should only be a word without space
								// here
								String SString = tokens.nextToken();
								int p = SString.indexOf(":");
								SubjectString = SString.substring(0, p);

								// Get Content
								ContentString = SString.substring(p + 1,
										SString.length()) + " ";
								while (tokens.hasMoreTokens()) {
									ContentString = ContentString
											+ tokens.nextToken() + " ";
								}
							} catch (Exception e) {

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
							//String GroupName1;
							int GroupID1;

							Object[] Sarray = new Object[3];
							Sarray = GetGroupNameAndID(GroupString);
							// if there is error getting group name and ID
							if (Sarray[2].equals("ERROR")) {
								Exception e = new Exception();
								throw e;
							} else {
								//GroupName1 = (String) Sarray[0];
								GroupID1 = (int) Sarray[1];
								
								JSONObject jsonObject = new JSONObject();
								jsonObject.put("Type", "UserList");
								
								JSONArray jsonArray1=new JSONArray();
															
								for (int i = 0; i < DataList.UserNum; i++) {
									User user = DataList.UserList.get(i);
									if (CheckInGroup(userID, GroupID1) == true) {
										jsonArray1.add(user.Name);
									}
								}
								jsonObject.put("Content", jsonArray1);
								os.writeBytes(jsonObject.toString()+"\r\n\r\n");
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
							//String GroupName1;
							int GroupID1;

							Object[] Sarray = new Object[3];
							Sarray = GetGroupNameAndID(GroupString);
							// if there is error getting group name and ID
							if (Sarray[2].equals("ERROR")) {
								Exception e = new Exception();
								throw e;
							} else {
								//GroupName1 = (String) Sarray[0];
								GroupID1 = (int) Sarray[1];
								SendNotification(username, GroupID1, "LEAVE");
								DeleteUser(userID);
								os.writeBytes("Successfully leave group "
										+ GroupID1 + "!!\n");
							}
						} catch (Exception e) {

						}
					}

					// IF EXIT
					if (CommandType.equals("%EXIT")) {
						break;
					}
					// if GROUPMESSAGE
					if (CommandType.equals("%GROUPMESSAGE")) {
						tokens.nextToken();
						int GetMessageID = Integer.parseInt(tokens.nextToken());
						Message GetMessage = DataList.MessageList
								.get(GetMessageID);
						
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("Type", "GetMessage");
						jsonObject.put("MessageID", GetMessage.ID);
						jsonObject.put("MessageGroupID", GetMessage.groupID);
						jsonObject.put("MessageSender", GetMessage.sender);
						jsonObject.put("MessagePostTime", GetMessage.Postdate);
						jsonObject.put("MessageSubject", GetMessage.Suject);
						jsonObject.put("MessageContent", GetMessage.Content);
						String outputS = jsonObject.toString();
						os.writeBytes(outputS);
						
						
						
					}

					// if GROUP
					if (CommandType.equals("%GROUPS")) {
						// print select groups
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("Type", "GroupList");
						
						JSONArray jsonArray1=new JSONArray();
													
						for (int i = 0; i < DataList.GroupList.size(); i++) {
							jsonArray1.add(DataList.GroupList.get(i));
							
						}
						jsonObject.put("Content", jsonArray1);
						os.writeBytes(jsonObject.toString());
						
					}
				} else {
					os.writeBytes("Undefined Command!!\n");
				}
			}

			// loop to wait for incoming data
			InputString = "";
			while (InputString.equals("")) {
				InputString = GetJsonString(br);
				if (!InputString.equalsIgnoreCase("")) {
					JSONObject jsonObject1 = (JSONObject)JsonParser.parse(InputString);
					s =(String) jsonObject1.get("cmd");
					break;
				}
			}
			// s = br.readLine();
		}// end while true
		os.close();

	}

	private boolean CheckInGroup(int userID, int groupID) {
		// check if user in a group
		boolean ans = false;

		ArrayList<Integer> arraylist = DataList.GroupSelection.get(userID);
		if (arraylist.contains(groupID))
			ans = true;
		return ans;
	}

	private void SendNotification(String username, int groupID,
			String activityType) {

		// String groupname = DataList.GroupList.get(groupID);
		if (activityType == "LEAVE") {
			// make output string
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("Type", "UserActivity");
			jsonObject.put("Activity", "LEAVE");
			jsonObject.put("User", username);
			jsonObject.put("Group", groupID);
			String outputS = jsonObject.toString();

			// send to other user in group
			for (int i = 0; i < DataList.UserNum; i++) {
				User user = DataList.UserList.get(i);
				if (CheckInGroup(user.ID, groupID) == true) {
					try {
						Socket clientSocket = user.Socket;
						DataOutputStream os1 = new DataOutputStream(
								clientSocket.getOutputStream());
						os1.writeBytes(outputS+"\r\n\r\n");
					} catch (Exception e) {
					}
				}
			}
		} else {// ativity type = Join
				// make output string
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("Type", "UserActivity");
			jsonObject.put("Activity", "JOIN");
			jsonObject.put("User", username);
			jsonObject.put("Group", groupID);
			String outputS = jsonObject.toString();

			// send to other user in group
			for (int i = 0; i < DataList.UserNum; i++) {
				User user = DataList.UserList.get(i);
				if ((CheckInGroup(user.ID, groupID) == true)
						&& (!user.Name.equals(username))) {
					try {
						Socket clientSocket = user.Socket;
						DataOutputStream os1 = new DataOutputStream(
								clientSocket.getOutputStream());
						os1.writeBytes(outputS+"\r\n\r\n");
					} catch (Exception e) {
					}
				}
			}

			// send to user itself
			for (int i = 0; i < DataList.UserNum; i++) {
				User user = DataList.UserList.get(i);
				if (CheckInGroup(user.ID, groupID) == true) {
					try {

						JSONObject jsonObject1 = new JSONObject();
						jsonObject1.put("Type", "UserActivity");
						jsonObject1.put("Activity", "JOIN");
						jsonObject1.put("User", user.Name);
						jsonObject1.put("Group", groupID);
						String outputS1 = jsonObject1.toString();

						Socket clientSocket = socket;
						DataOutputStream os1 = new DataOutputStream(
								clientSocket.getOutputStream());
						os1.writeBytes(outputS1);
					} catch (Exception e) {
					}
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
		message.groupID=groupID;
		message.ID = DataList.MessageNum - 1;
		message.sender = username;
		message.senderID = userID;
		message.Postdate = new Date();
		DataList.MessageList.add(message.ID, message);
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Type", "UserPost");
		jsonObject.put("MessageID", message.ID);
		jsonObject.put("MessageGroupID", message.groupID);
		jsonObject.put("MessageSender", message.sender);
		jsonObject.put("MessagePostTime", message.Postdate);
		jsonObject.put("MessageSubject", message.Suject);
		String outputS = jsonObject.toString();
		
		for (int i = 0; i < DataList.UserNum; i++) {
			User user = DataList.UserList.get(i);
			if (CheckInGroup(user.ID, groupID) == true) {
				try {
					Socket clientSocket = user.Socket;
					DataOutputStream os1 = new DataOutputStream(
							clientSocket.getOutputStream());
					os1.writeBytes(outputS);
				} catch (Exception e) {
				}
			}
		}

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

	private void PostLastTwoMessage(int groupID, String username ) {
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