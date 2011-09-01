import socket
import struct

serversocket = socket.socket(
    socket.AF_INET, socket.SOCK_STREAM)

#bind the socket to a public host,
# and a well-known port
hostname = socket.gethostname()
print "Binding to", hostname
serversocket.bind((hostname, 1337))

#become a server socket
serversocket.listen(5) 

while 1:
    #accept connections from outside
    (clientsocket, address) = serversocket.accept()
    length_packet = clientsocket.recv(4)
    length = struct.unpack("i", length_packet)[0]
    print length
    message = clientsocket.recv(length)
    exec message
