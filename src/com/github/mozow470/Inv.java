package com.github.mozow470.servermenu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.mozow470.serverstats.ServerInfo;
import com.github.mozow470.serverstats.lib.Group;
import com.github.mozow470.serverstats.menu.Menu;

public class Inv extends Menu{

	public Inv(String name, int slot,Group group) {
		super(name, slot);
		this.setGroup(group);
	}

// 1秒ごとにmenuを更新する onRunは1秒に1回呼び出される
	@Override
	public void onRun() {
		if(this.getGroup() != null){
			int tick = 0;
			for(ServerInfo info:this.getGroup().getServers()){
				if(info.isOnline()){
					this.addOnlineServer(info, tick);
					tick++;
				}
			}
		}

	}

	public void addOnlineServer(ServerInfo serverinfo, int slot){
		String server_name = serverinfo.getMoveString().getScreen();
		int online_players = serverinfo.online_players;
		int max_players =  serverinfo.max_players;
		ItemStack item = null;
		List<String> lore = new ArrayList<String>();

		lore.add(" ");
		lore.add(ChatColor.GRAY + "Players: " + ChatColor.GOLD + serverinfo.getOnlinePlayers()+ChatColor.DARK_GRAY+"/"+ChatColor.GOLD+serverinfo.getMaxPlayers());
		lore.add(ChatColor.GRAY + "Version: " + ChatColor.RESET + serverinfo.getVersion());
		lore.add("  ");
		lore.add(ChatColor.GRAY + "Status: " + serverinfo.getStats());

		if(serverinfo.isFull()){
			item = new ItemStack(Material.GOLD_BLOCK, 1, (byte)0);
		} else {
			item = new ItemStack(Material.EMERALD_BLOCK, 1, (byte)0);
		}
		item.setAmount(serverinfo.getOnlinePlayers());

		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		meta.setDisplayName("" + ChatColor.GREEN + ChatColor.BOLD + ChatColor.UNDERLINE +server_name);

		item.setItemMeta(meta);
		this.getInventory().setItem(slot, item);
	}

	@Override
	public void openInventoryEvent() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void setupInventory() {
		// TODO 自動生成されたメソッド・スタブ

	}

}
