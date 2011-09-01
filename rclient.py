import socket
import struct

clientsocket = socket.socket(
    socket.AF_INET, socket.SOCK_STREAM)

clientsocket.connect(('siebl-0218-10.ews.illinois.edu', 7000)) 

while 1:
	message = raw_input("Enter a command: ")
	size = len(message) 
	size_packet = struct.pack("i", size) 
	clientsocket.send(size_packet) 
	clientsocket.send(message) 
