package hwk_upr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class serverChatThread extends Thread {
	Socket csocket;

	public serverChatThread(Socket csocket) {
	  this.csocket = csocket;
	}

    public void run() {
      try {
    	BufferedReader serverIn = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
        while(serverIn.ready()) {
        	System.out.println(serverIn.readLine());
        }
        
        serverIn.close();
        csocket.close();
      } catch (IOException e) {
        System.out.println(e);
      }
    }
}
