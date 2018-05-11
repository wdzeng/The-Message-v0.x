package the_message_server;

public class MessageTimer extends Thread {

	int who;
	String mess;
	int time;

	public MessageTimer(int who, String mess, int time) {
		this.who = who;
		this.mess = mess;
		this.time = time;
	}
	
	@Override
	public void run(){
		
	}
	
}
