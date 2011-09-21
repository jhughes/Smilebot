import socket
import struct

clientsocket = socket.socket(
    socket.AF_INET, socket.SOCK_STREAM)

clientsocket.connect(('192.168.1.13', 5000)) 

while 1:
	message = raw_input("Enter a command: ")
	size = len(message) 
	size_packet = struct.pack("i", size) 
	clientsocket.send(size_packet) 
	clientsocket.send(message) 
