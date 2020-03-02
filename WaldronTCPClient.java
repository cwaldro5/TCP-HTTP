import java.io.*;
import java.net.*;
import java.time.*;

public class WaldronTCPClient {
    public static void main(String[] args) throws IOException {

        Socket tcpSocket = null;
        Clock clock = Clock.systemDefaultZone();
        PrintWriter socketOut = null;
        BufferedReader socketIn = null;
        BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter output;
        String serverAdd;
        String fromServer =  "";
        String fromUser;
        String methodName, fileName, version, agent, request, nxtLine, data, userInput;
        String response = "";
        long startTime, connectionTime, responseTime;
        int blankCounter = 0;
        boolean keepGoing = true;
        boolean firstRequest = true;
        

        System.out.println("Enter the address of the server");
        serverAdd = sysIn.readLine();
        
        startTime = clock.millis();
        try {
            tcpSocket = new Socket(serverAdd, 5320);
            socketOut = new PrintWriter(tcpSocket.getOutputStream(), true);
            socketIn = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + serverAdd);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: "  + serverAdd);
            System.exit(1);
        }
        connectionTime = clock.millis() - startTime;
        System.out.println("Connection time: " + connectionTime + "ms");

        while(keepGoing){
            System.out.println("Enter the http method you would like to use");
            methodName = sysIn.readLine();
        
            System.out.println("Enter the name of the file you would like from the server. (Ex: someFile.txt)");
            fileName = sysIn.readLine();
        
            System.out.println("Enter the version of http you would like to use.");
            version = sysIn.readLine();
        
            System.out.println("Enter the agent you would like to use. (Ex: Chrome)");
            agent = sysIn.readLine();
            System.out.println("");
        
            System.out.println("Message sent to server:");
            request = (methodName.toUpperCase() + " /" + fileName + " HTTP/" + version + "\r\n" +
                "Host: " + serverAdd + "\r\n" +
                "User-Agent: " + agent + "\r\n");
        
            System.out.println(request);
            socketOut.println(request); 
            startTime = clock.millis();
            if(firstRequest){
		         fromServer = socketIn.readLine();
               response = fromServer;
               firstRequest = false;
            }
               
            while(blankCounter < 1){
                fromServer = socketIn.readLine();
                if(!(fromServer.equals(""))){
                    response = response + "\r\n" + fromServer;
                }
                else{
                     blankCounter++;
                } 
            }
            blankCounter = 0;
            responseTime = clock.millis() - startTime;
            System.out.println("RTT = " + responseTime + "ms");
            System.out.println("");
            System.out.println("Message recieved from server:");
            System.out.println(response);
            System.out.println("");
            String[] lines = response.split("\r\n", 5);
            if(response.contains("200")){
            
               data = socketIn.readLine();
               while(blankCounter < 4 && fromServer != null){
                  fromServer = socketIn.readLine();
             
                  if (fromServer.equals("")){
                        blankCounter++;
                        data = data + "\r\n";
                  }
            
                  else{
                        blankCounter = 0;
                        data = data + "\r\n" + fromServer;
                  }
               }
               blankCounter = 0;
               System.out.println(data);
               fileName = "./" + fileName;
               try {
                   File file = new File(fileName);
                   file.createNewFile();
                   FileOutputStream fos = new FileOutputStream(file);
                   OutputStreamWriter osw = new OutputStreamWriter(fos);    
                   Writer fileWriter = new BufferedWriter(osw);
                   fileWriter.write(data);
                   fileWriter.close();
               } 
               catch ( IOException e ) {
                 System.err.println("Unable to write to the file");
               }
            }
            else{
               System.out.println("The file requseted could not be found or command was unrecognized.");
            }
            System.out.println("Would you like to repeat this process? (enter yes or no)");
            userInput = sysIn.readLine();
            if(userInput.equalsIgnoreCase("yes")){
                  keepGoing = true;
            }
            else{
                  keepGoing = false;
            }
            response = "";
        }

        socketOut.close();
        socketIn.close();
        sysIn.close();
        tcpSocket.close();
    }
}