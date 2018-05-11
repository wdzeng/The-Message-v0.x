package the_message_server;

@SuppressWarnings("serial")
public class LogicException extends TheMessageException{

	public LogicException(String ms) {
		super(ms);
		printStackTrace();
	}
	
}
