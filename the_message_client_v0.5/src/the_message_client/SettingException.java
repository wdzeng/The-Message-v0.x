package the_message_client;

@SuppressWarnings("serial")
class SettingException extends TheMessageException {

	public SettingException(String ms) {
		super(ms);
	}
	
	public SettingException() {
		super("");
	}

}
