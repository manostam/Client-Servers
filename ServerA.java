import java.io.*;
import java.net.*;
// laptop
public class ServerA {
    public static void main(String args[]) throws IOException 
    {

        // create a server socket on port number 9090
        ServerSocket serverSocket = new ServerSocket(9090); // ServerSocket: This socket waits for incoming client requests. It listens for connections on a specific port.
        System.out.println("Server A is running and waiting for client connection...");

        // Accept incoming client connection
        Socket clientSocket = serverSocket.accept(); // Socket: Once a connection is established, the server uses this socket to communicate with the client.
                                                     // The accept() method blocks(just sits there) until a client connects to the server.
        System.out.println("Client connected!");

        // Setup input and output streams for communication with the client
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); // The getOutputStream() method is used to send data to the client.
        DataOutputStream dataOut = new DataOutputStream( clientSocket.getOutputStream() );

        String fileName = "nada";

        // loop and wait for requests until the Client sends message to cut the communication
        while ( !fileName.equals("stopServer") ) {
            // Read file name from client
            fileName = in.readLine();
            System.out.println("Client requests file: " + fileName);
            String fileAddress = "serverFiles/" + fileName;
            System.out.println("at the address: " + fileAddress);

            File file = new File(fileAddress);
            if (file.exists()) {
                System.out.println("Exists, sending size");
                // Send file size
                dataOut.writeLong(file.length());                           // Sends the file size (as a long value) to the client. This allows the client to prepare for receiving the data and ensure the entire file is transferred.
                
                // Send file content
                try (FileInputStream fileIn = new FileInputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fileIn.read(buffer)) != -1) {       // fileIn.read(buffer)-> Reads up to buffer.length bytes from the file and stores them in the buffer array. Returns the number of bytes read or -1 if the end of the file is reached.
                        dataOut.write(buffer, 0, bytesRead);                // Sends the data stored in the buffer to the client, starting from index 0 and writing bytesRead bytes.
                    }
                }
                System.out.println("File " + fileName + " sent successfully.");
            } else {
                System.out.println("File not found: " + fileName);
                dataOut.writeLong(0);                                       // If the file does not exist, sends 0 to the client to indicate that no file will be transmitted.
            }
        } 
        
        
        // Close the client socket
        clientSocket.close();
        // Close the server socket
        serverSocket.close();
    }
}




























