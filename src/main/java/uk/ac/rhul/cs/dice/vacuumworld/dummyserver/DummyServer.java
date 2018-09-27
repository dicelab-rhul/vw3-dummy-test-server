package uk.ac.rhul.cs.dice.vacuumworld.dummyserver;

import org.cloudstrife9999.logutilities.LogUtils;

public class DummyServer {

    private DummyServer() {}
    
    public static void main(String[] args) {
	LogUtils.log("Dummy model started.");
	
	int port = 17777;
	
	DummyServerListener server = new DummyServerListener(port);
	server.init();
    }
}