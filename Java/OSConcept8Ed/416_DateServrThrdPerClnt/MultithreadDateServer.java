// A date server which serves client request with a different thread
import java.util.*;
import java.net.*;
import java.lang.*;
import java.io.*;

class RequestProcessor implements Runnable {

	private OutputStream os;
	private boolean autoFlush;

	public RequestProcessor (OutputStream os, boolean autoFlush) {
		this.os = os;
		this.autoFlush = autoFlush;
	}

	public void run () {
		
		Thread currentThread = Thread.currentThread();
		System.out.println("Current thread name: " + currentThread.getName());
		System.out.println("Current thread ID: " + currentThread.getId());
		PrintWriter pw = new PrintWriter(os, autoFlush);
		pw.println(new Date().toString());
	}
}

public class MultithreadDateServer {
	public static void main (String[] args) {
		try {
			while (true) {
			    ServerSocket sock = new ServerSocket(6013);
				Socket client = sock.accept();

				Thread thrd = new Thread(new RequestProcessor(client.getOutputStream(), true));
				thrd.start();
				try {
					thrd.join();
				} catch (InterruptedException ie) { }
				sock.close();
			}
		} catch (IOException ioe) {
			System.err.println (ioe);
		}
	}
}
