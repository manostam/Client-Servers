The program consists of three java files (Client.java, ServerA.java, ServerB.java). 
Purpose of the program is the communication of the Client (from a device e.g. laptop, smartphone) with the Servers (ServerA, ServerB) to get some files from them.
	The communication begins by executing the Client.java from the command line, passing as parameters the n_A, n_B, ip_A, ip_B (where n_A, n_B is the ratio of getting files from each server and  ip_A, ip_B the corresponding ip’s of the devices where ServerA.java and ServerB.java run). The servers should be run beforehand at each device and wait for the client to connect with them. 
	The whole process is based on java sockets.
