package com.github.mozow470.tutorial;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Tutorial extends JavaPlugin implements Listener{
	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(this, this); //リスナ登録
	}
	public void onDisable()
	{
		
	}
	@EventHandler
	public void onJoinEvent(PlayerJoinEvent e)
	{
		Player player=e.getPlayer();
		e.setJoinMessage("Hello "+player.getName().toString());
		
	}
	
	
}
