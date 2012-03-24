package com.titankingdoms.nodinchan.titanchat.command;

import java.util.logging.Logger;
import com.nodinchan.loader.Loader;
import com.titankingdoms.nodinchan.titanchat.TitanChat;

public final class CommandLoader extends Loader<Command> {
	
	public CommandLoader(TitanChat plugin) {
		super(plugin, plugin.getCommandDir(), new Object[0]);
	}
	
	@Override
	public Logger getLogger() {
		return TitanChat.getInstance().getLogger();
	}
}