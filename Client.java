import java.io.*;
import java.net.*;

public class Client {
    public static void main(String args[]) throws IOException 
    {
       
        

        // separating the cmd arguments of client: n_A n_B IP_A IP_B
        int n_A = Integer.parseInt(args[0]);
        System.out.println(n_A);
        int n_B = Integer.parseInt(args[1]);
        System.out.println(n_B);
        String IP_A = args[2];
        System.out.println(IP_A);
        String IP_B = args[3];
        System.out.println(IP_B);
        
        long startTime = System.nanoTime(); // Start timing

        // create a socket to connect to the server running on localhost at port number 9090
        Socket socketA = new Socket(IP_A, 9090);

        // create a socket to connect to the server running on localhost at port number 9095
        Socket socketB = new Socket(IP_B, 9095);

        // Setup output stream to send data to the server(s)
         PrintWriter outA = new PrintWriter(socketA.getOutputStream(), true); // suitable for text etc
         PrintWriter outB = new PrintWriter(socketB.getOutputStream(), true); // suitable for text etc
        //DataOutputStream outA = new DataOutputStream( socketA.getOutputStream() );
        //DataOutputStream outB = new DataOutputStream( socketB.getOutputStream() );  

        // Setup input stream to receive data from the server
        BufferedReader inA = new BufferedReader(new InputStreamReader(socketA.getInputStream())); // suitable for text etc
        BufferedReader inB = new BufferedReader(new InputStreamReader(socketB.getInputStream())); // suitable for text etc
        DataInputStream dataInA = new DataInputStream( socketA.getInputStream() );
        DataInputStream dataInB = new DataInputStream( socketB.getInputStream() );

/*
        // Send message to the server(s)
        out.println("Hello from client!");
        out2.println("Hello from client!");

        // Receive response from the server
        String responseA = inA.readLine();
        System.out.println("ServerA says: " + response);

        String responseB = inB.readLine();
        System.out.println("ServerB says: " + response2);
*/

        String defaultFilePrefix = "s";
        String defaultFileSuffix = ".m4s";
        int requestTimesFromA = 0;
        requestTimesFromA += n_A;
        int requestTimesFromB = 0;
        // requestTimesFromB += n_B;
        String fileNumberText = null;
        for (int i = 1; i < 161; i++) { // condition so that I take all the files

            System.out.println("i=" + i + " requestTimesFromA=" + requestTimesFromA + " requestTimesFromB=" + requestTimesFromB);
            
            // single digit number
            if (i < 10) {
                fileNumberText = "00" + i;
            }
            else if (i < 100) { // double digit number
                fileNumberText = "0" + i;
            }
            else {  // (i < 160)    // triple digit number
                fileNumberText = String.valueOf(i);
            }

            String fileName = defaultFilePrefix + fileNumberText + defaultFileSuffix;

            // defining from which server we should request the file
            if (requestTimesFromA > 0) {    // requesting files from ServerA as long as its ratio is not zero
                System.out.println("sending to A the filename:" + fileName);
                // Send filename to serverA
                outA.println(fileName);
                

                // Receive file size
                long fileSize = dataInA.readLong(); // Reads the file size sent by the server as a long value. If the file size is 0, it means the file does not exist on the server.
                if (fileSize > 0) {
                    System.out.println("Receiving file: " + fileName + " (" + fileSize + " bytes)");
    
                    // Save the file locally
                    try (FileOutputStream fileOut = new FileOutputStream("received_" + fileName)) { // Creates a new file (or overwrites an existing file) with the specified name. This file will be used to store the received data
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        long totalRead = 0;
    
                        while (totalRead < fileSize &&                                              // Ensures the loop continues until the entire file (based on its size) is received.
                               (bytesRead = dataInA.read(buffer)) != -1) {                          // Reads data from the server into the buffer array. Returns the number of bytes read or -1 if there is no more data.
                            fileOut.write(buffer, 0, bytesRead);                                    // Writes bytesRead bytes from the buffer array to the file. This saves the binary data received from the server.
                            totalRead += bytesRead;                                                 // Keeps track of how many bytes have been received so far to ensure the correct file size is processed.
                        }
    
                        System.out.println("File " + fileName + " received and saved as received_" + fileName);
                    }
                } else {
                    System.out.println("File not found on the server.");
                }
                


                // --- about alternating servers
                requestTimesFromA -= 1;
                if ( requestTimesFromA == 0 ) { // ok time to request from serverB
                    requestTimesFromB += n_B;    // increment -not just set (creates reference)- the initial ratio
                }
            }
            else {  
                System.out.println("sending to B the filename:" + fileName);
                // Send filename to serverB
                outB.println(fileName);
                

                // Receive file size
                long fileSize = dataInB.readLong();
                if (fileSize > 0) {
                    System.out.println("Receiving file: " + fileName + " (" + fileSize + " bytes)");
    
                    // Save the file locally
                    try (FileOutputStream fileOut = new FileOutputStream("received_" + fileName)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        long totalRead = 0;
    
                        while (totalRead < fileSize &&
                               (bytesRead = dataInB.read(buffer)) != -1) {
                            fileOut.write(buffer, 0, bytesRead);
                            totalRead += bytesRead;
                        }
    
                        System.out.println("File " + fileName + " received and saved as received_" + fileName);
                    }
                } else {
                    System.out.println("File not found on the server.");
                }


                // --- about alternating servers
                requestTimesFromB -= 1;
                if ( requestTimesFromB == 0 ) { // ok time to request again from serverA
                    requestTimesFromA += n_A;    // increment -not just set (creates reference)- the initial ratio   
                }
            }

            

        }
        
        outA.println("stopServer");
        outB.println("stopServer");


        long endTime = System.nanoTime(); // End timing
        long elapsedTime = endTime - startTime; // Calculate elapsed time in nanoseconds
        double elapsedTimeInSeconds = elapsedTime / 1_000_000_000.0; // Convert to seconds
        System.out.println("Time taken to receive all files: " + elapsedTimeInSeconds + " seconds");




        // Close the socket
        socketA.close();
        socketB.close();
    }
}