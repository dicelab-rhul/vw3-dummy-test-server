package uk.ac.rhul.cs.dice.vacuumworld.dummyserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.cloudstrife9999.logutilities.LogUtils;

import uk.ac.rhul.cs.dice.vacuumworld.vwcommon.VWMessageCodes;
import uk.ac.rhul.cs.dice.vacuumworld.vwcommon.VacuumWorldMessage;

public class DummyServerListener {
    private int port;
    private ServerSocket server;
    private Socket socketWithController;
    private InputStream in;
    private OutputStream out;
    private ObjectInputStream fromController;
    private ObjectOutputStream toController;
    private VacuumWorldMessage latestFromController;
    
    public DummyServerListener(int port) {
	this.port = port;
    }

    public void init() {
	try {
	    this.server = new ServerSocket(this.port);
	    
	    LogUtils.log("Model here: waiting for connections...");
	    
	    this.socketWithController = this.server.accept();
	    
	    LogUtils.log("Model here: a controller attempted a connection: " + this.socketWithController.getRemoteSocketAddress() + ".");
	    
	    this.out = this.socketWithController.getOutputStream();
	    this.in = this.socketWithController.getInputStream();
	    this.toController = new ObjectOutputStream(this.out);
	    this.fromController = new ObjectInputStream(this.in);
	    
	    doHandshake();
	}
	catch(Exception e) {
	    LogUtils.log(e);
	}
    }

    private void doHandshake() {
	LogUtils.log("Model here: waiting for the first handshake message...");
	
	receiveHCM();
	receiveHVM();
	
	LogUtils.log("Model here: handshake completed!");
    }

    private void receiveHCM() {
	try {
	    this.latestFromController = (VacuumWorldMessage) this.fromController.readObject();
	    parseHCM();
	    sendHMC();
	}
	catch(Exception e) {
	    LogUtils.log(e);
	}	
    }

    private void sendHMC() throws IOException {
	sendTo(this.toController, new VacuumWorldMessage(VWMessageCodes.HELLO_CONTROLLER_FROM_MODEL, null));
    }

    private void receiveHVM() {
	try {
	    this.latestFromController = (VacuumWorldMessage) this.fromController.readObject();
	    parseHVM();
	    sendHMV();
	}
	catch(Exception e) {
	    LogUtils.log(e);
	}
    }

    private void sendHMV() throws IOException {
	sendTo(this.toController, new VacuumWorldMessage(VWMessageCodes.HELLO_VIEW_FROM_MODEL, null));
    }
    
    private void sendTo(ObjectOutputStream to, VacuumWorldMessage message) throws IOException {
	LogUtils.log("Model here: sending" + message.getCode() + " to the controller...");
	
	to.reset();
	to.writeObject(message);
	to.flush();
    }

    private void cycle() {
	// TODO Auto-generated method stub
    }
    
    private void parseHCM() {
	parseMessageType(VWMessageCodes.HELLO_MODEL_FROM_CONTROLLER, this.latestFromController);
    }
    
    private void parseHVM() {
	parseMessageType(VWMessageCodes.HELLO_MODEL_FROM_VIEW, this.latestFromController);
    }    
    
    private void parseMessageType(VWMessageCodes expected, VacuumWorldMessage message) {
	VWMessageCodes receivedCode = message.getCode();
	
	if(!expected.equals(receivedCode)) {
	    throw new IllegalArgumentException("Expected" + expected + ", got " + receivedCode + " instead.");
	}
	else {
	    LogUtils.log("Model here: received " + receivedCode + " from the controller.");
	}
    }
}