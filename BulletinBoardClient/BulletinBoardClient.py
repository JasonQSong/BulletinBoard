import socket
import thread
import sys
import curses
import json


ClientSocket=None
ScreenWin=None
ScreenSize=None
MsgPad=None
MsgPadLines=None
MsgPadPos=None
TypeWin=None

def InitScreen(stdscr):
    global ScreenWin
    global ScreenSize
    global MsgPad
    global MsgPadLines
    global MsgPadPos
    global TypeWin
    ScreenWin=stdscr
    ScreenSize=ScreenWin.getmaxyx()
    MsgPad=curses.newpad(1024,80)
    MsgPadLines=0;
    MsgPadPos=0;
    TypeWin=curses.newwin(1,ScreenSize[1]-1,ScreenSize[0]-1,0)
    curses.echo()

def ReadLineFromTypeWin():
    global TypeWin
    s=TypeWin.getstr(0,0,80)
    TypeWin.clear()
    TypeWin.refresh()
    return s

def WriteLineToMsgPad(line):
    global ScreenWin
    global ScreenSize
    global MsgPad
    global MsgPadLines
    global MsgPadPos
    MsgPad.addstr(MsgPadLines,0,line)
    MsgPadLines=MsgPadLines+line.count('\n')
    MsgPadLines=MsgPadLines+1
    ScreenSize=ScreenWin.getmaxyx()
    MsgPadPos=MsgPadLines-ScreenSize[0]+1
    if MsgPadPos<0:
        MsgPadPos=0
    MsgPad.refresh(MsgPadPos,0,0,0,ScreenSize[0]-1,ScreenSize[1]-1)

def SolvePackage(JsonStr):
    WriteLineToMsgPad('[RECV]'+JsonStr)
    JsonObj=json.loads(JsonStr)
    OutputString=""
    if JsonObj['Type']=='UserList':
        UserList=JsonObj['Content']
        for UserName in UserList:
            WriteLineToMsgPad('User:'+UserName)
    elif JsonObj['Type']=='GroupList':
        GroupList=JsonObj['Content']
        for GroupName in GroupList:
            WriteLineToMsgPad('Group:'+GroupName)
    elif JsonObj['Type']=='UserActivity':
        WriteLineToMsgPad('User: '+JsonObj['User']+' '+JsonObj['Activity'] +' group:'+str(JsonObj['Group']))
    elif JsonObj['Type']=='UserPost':
        WriteLineToMsgPad('MessageID: '+str(JsonObj['MessageID']))
        WriteLineToMsgPad('MessageGroupID: '+str(JsonObj['MessageGroupID'] ))
        WriteLineToMsgPad('MessageSender: '+JsonObj['MessageSender'] )
        WriteLineToMsgPad('MessagePostTime: '+JsonObj['MessagePostTime'] )
        WriteLineToMsgPad('MessageSubject: '+JsonObj['MessageSubject'] )
    elif JsonObj['Type']=='GetMessage':
        WriteLineToMsgPad('MessageID: '+str(JsonObj['MessageID']))
        WriteLineToMsgPad('MessageGroupID: '+str(JsonObj['MessageGroupID'] ))
        WriteLineToMsgPad('MessageSender: '+JsonObj['MessageSender'] )
        WriteLineToMsgPad('MessagePostTime: '+JsonObj['MessagePostTime'] )
        WriteLineToMsgPad('MessageSubject: '+JsonObj['MessageSubject'] )
        WriteLineToMsgPad('MessageContent: '+JsonObj['MessageContent'] )
    elif JsonObj['Type']=='Error':
        pass
        # WriteLineToMsgPad(JsonObj['Content'])
    else:
        WriteLineToMsgPad('Unknown Message')


    # PackageObj=json

def KeepRead(socket,b):
    ReadBuffer=''
    while True:
        ReceivedString=socket.recv(2048)
        if len(ReceivedString)>0:
            ReadBuffer+=ReceivedString;
            if ReadBuffer.find('\r\n\r\n')>0:
                JsonStr=ReadBuffer[:ReadBuffer.find('\r\n\r\n')]
                JsonStr=JsonStr.strip()
                ReadBuffer=ReadBuffer[ReadBuffer.find('\r\n\r\n'):]
                ReadBuffer=ReadBuffer.lstrip()
                SolvePackage(JsonStr)


def InitSocket(host,port):
    global ClientSocket
    ClientSocket=socket.socket(socket.AF_INET,socket.SOCK_STREAM)
    ClientSocket.connect((host,port))
    thread.start_new_thread(KeepRead,(ClientSocket,None))

def SendToSocket(buf):
    global ClientSocket
    sent=ClientSocket.send(buf)
    return sent

GroupPost=None
def main(stdscr):
    global GroupPost
    InitScreen(stdscr)
    InitSocket(sys.argv[1],int(sys.argv[2]))
    WriteLineToMsgPad('[LOCAL] Connected to '+sys.argv[1]+':'+sys.argv[2])
    while True:
        cmd=ReadLineFromTypeWin()
        if cmd.lower().startswith('%grouppost'):
            tmpGroupPost=cmd[10:]
            tmpGroupPost=tmpGroupPost.strip();
            if tmpGroupPost.find(' ')>0:
                tmpGroupPost=tmpGroupPost[:tmpGroupPost.find(' ')]
            GroupPost=tmpGroupPost
            WriteLineToMsgPad('[LOCAL]Messages will be sent to '+GroupPost)
            continue
        JsonStr=json.dumps({
            'type':'NONE'
            })
        if cmd.startswith('%'):
            JsonStr=json.dumps({
                'type':'COMMAND',
                'cmd':cmd
                })
        elif GroupPost==None:
            WriteLineToMsgPad('[LOCAL] GroupPost haven\'t been set yet')
            continue
        else:
            JsonStr=json.dumps({
                'type':'COMMAND',
                'cmd':'%grouppost '+GroupPost+' '+cmd
                })
        WriteLineToMsgPad('[SEND]'+JsonStr)
        sent=SendToSocket(JsonStr+'\r\n\r\n')

if __name__=='__main__':
    curses.wrapper(main)


