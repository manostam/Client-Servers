import java.io.*;
import java.net.*;
// smartphone
public class ServerB {
    public static void main(String args[]) throws IOException 
    {

        // create a server socket on port number 9095
        ServerSocket serverSocket = new ServerSocket(9095); // ServerSocket: This socket waits for incoming client requests. It listens for connections on a specific port.
        System.out.println("Server A is running and waiting for client connection..."); // yy forgot to change it before passing to smartphone

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
                dataOut.writeLong(file.length());
                // Send file content
                try (FileInputStream fileIn = new FileInputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fileIn.read(buffer)) != -1) {
                        dataOut.write(buffer, 0, bytesRead);
                    }
                }
                System.out.println("File " + fileName + " sent successfully.");
            } else {
                System.out.println("File not found: " + fileName);
                dataOut.writeLong(0); // Indicate file not found
            }
        } 
        
        
        // Close the client socket
        clientSocket.close();
        // Close the server socket
        serverSocket.close();
    }
}




























