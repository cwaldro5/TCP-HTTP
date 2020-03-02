import java.net.*;
import java.io.*;

public class WaldronTCPServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverTCPSocket = null;
        boolean listening = true;

        try {
            serverTCPSocket = new ServerSocket(5320);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 5320.");
            System.exit(-1);
        }

        while (listening){
	    		new WaldronTCPServerThread(serverTCPSocket.accept()).start();
		  }
			
        serverTCPSocket.close();
    }
}