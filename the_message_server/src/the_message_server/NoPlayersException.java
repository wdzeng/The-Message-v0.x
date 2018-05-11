package the_message_server;

@SuppressWarnings("serial")
public class NoPlayersException extends TheMessageException{

	public NoPlayersException(){
		super();
	}
	
	public NoPlayersException(String ms){
		super(ms);
	}
	
}
