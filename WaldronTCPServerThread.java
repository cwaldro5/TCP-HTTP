// Esc :wq -> vi save and quits
import java.net.*;
import java.io.*;
import java.time.*;
import java.util.*;
public class WaldronTCPServerThread extends Thread {
    private Socket clientTCPSocket = null;

    public WaldronTCPServerThread(Socket socket) {
		super("WaldronTCPServerThread");
		clientTCPSocket = socket;
    }
    
    public void run() {
       boolean keepAlive = true;
         while(keepAlive){
         try {
	 	      PrintWriter cSocketOut = new PrintWriter(clientTCPSocket.getOutputStream(), true);
	  		   BufferedReader cSocketIn = new BufferedReader(
			   	    new InputStreamReader(
			   	    clientTCPSocket.getInputStream()));

            Clock clock = Clock.systemDefaultZone();
            Date currDate;
            String toClient, status, version;
            String fromClient = "";
            String recievedMsg = "";
            String fileName = "";
            String fileContents;
            String[] splitMsg;
            String[] splitMsgLn;
			   int blankCounter = 0; 
	 	      long currTime;
            boolean fileFound = false;
         
         
                  while((fromClient != null) && blankCounter < 1){
                        fromClient = cSocketIn.readLine();
                        if(!(fromClient.equals(""))){
                              System.out.println(fromClient);
                              recievedMsg = recievedMsg + "\r\n" + fromClient;
                        }
                        else{
                              recievedMsg = recievedMsg + "\r\n";
                              blankCounter++;
                        }
                  }
                  System.out.println("");
                  blankCounter = 0;
            
                  splitMsgLn = recievedMsg.split("\r\n", 5);
            
                  splitMsg = splitMsgLn[1].split(" ", 4);

                  fileName = "." + splitMsg[1];
                  File wanted = new File(fileName); 
                  version = splitMsg[2];
            
                  if(splitMsg[0].equals("GET")){
                        if(wanted.exists()){
                              status = "200 OK";
                              fileFound = true;
                        }
                        else{
                              status = "404 Not Found";
                        }
                   }
                   else{
                        status = "400 Bad Request";
                   }
          
                   currTime = clock.millis();
                   currDate = new Date(currTime);
          
                   if(fileFound){
                        Scanner fileScanner = new Scanner(wanted);
                        fileScanner.useDelimiter("\\Z");
                        fileContents = fileScanner.next();   
                        toClient = (version + " " + status + "\r\n" +
                             "Date : " + currDate + "\r\n" +
                             "Apache/2.0.52 (CentOS)" + "\r\n" +
                             "\r\n" + 
                             fileContents +
                             "\r\n" +
                             "\r\n" +
                             "\r\n" +
                             "\r\n");
                   }    
                   else{
                        toClient = (version + " " + status + "\r\n" +
                             "Date : " + currDate + "\r\n" +
                             "Apache/2.0.52 (CentOS)" + "\r\n");
                   }
             
                   System.out.println("Sent Message:");
                   System.out.println(toClient);
                   cSocketOut.println(toClient);
         }
			
		    catch (IOException e) {
		    e.printStackTrace();
         }
         }
		} 
     }
