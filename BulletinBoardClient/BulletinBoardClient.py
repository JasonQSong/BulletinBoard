import socket
import thread
import sys
import curses


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


def KeepRead(socket,b):
    while True:
        ReceivedString=socket.recv(2048)
        if len(ReceivedString)>0:
            sys.stdout.write(ReceivedString)

def InitSocket():
    ClientSocket=socket.socket(socket.AF_INET,socket.SOCK_STREAM)
    ClientSocket.connect(('localhost',7001))
    thread.start_new_thread(KeepRead,(ClientSocket,None))
    '''
    while True:
        cmd=raw_input()+'\n'
        sent=ClientSocket.send(cmd);
    '''


def main(stdscr):
    global ScreenWin
    global ScreenSize
    global MsgPad
    global MsgPadLines
    global MsgPadPos
    global TypeWin
    InitScreen(stdscr)
    # InitSocket()
    while True:
        ScreenSize=stdscr.getmaxyx()
        s=TypeWin.getstr(0,0,80)
        TypeWin.clear()
        TypeWin.refresh()

        MsgPad.addstr(MsgPadLines,0,s)
        MsgPadLines=MsgPadLines+1
        ScreenSize=stdscr.getmaxyx()
        MsgPadPos=MsgPadLines-ScreenSize[0]+1
        if MsgPadPos<0:
            MsgPadPos=0
        MsgPad.refresh(MsgPadPos,0,0,0,ScreenSize[0]-1,ScreenSize[1]-1)


if __name__=='__main__':
    curses.wrapper(main)


