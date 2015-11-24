package edu.osu.bulletinboard;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

//要不要存一些数据到本地文件？
public class DataList {
	public static ArrayList<Message> MessageList = new ArrayList<Message>();//Store Messages posted
	public static ArrayList<User> UserList = new ArrayList<User>();//Store current users
	public static int UserNum=0,MessageNum=0,UserMaxID=-1;
	
	public static HashSet<Socket> ClientSocketList= new HashSet<Socket>();//Store all clients' socket
	
	public static ArrayList<String> GroupList= new ArrayList<String>();//store what group we have
	public static ArrayList<Integer> GroupIDList= new ArrayList<Integer>();//store what group we have
	
	public static boolean[][] GroupSelectionList =new boolean[100][10]; //tag what groups a user join
	public static ArrayList<ArrayList<Integer>> GroupSelection= new ArrayList<ArrayList<Integer>>();
	public static HashSet<String> CheckCommandType = new HashSet<String>();//store what command we have
}
