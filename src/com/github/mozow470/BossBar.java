package com.github.mozow470.guns.utils;

import net.minecraft.server.v1_8_R3.EntityEnderDragon;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.WorldServer;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import com.github.mozow470.guns.packet.PacketUtil;
import com.github.mozow470.guns.support.GunPlayer;

public class BossBar {

	public GunPlayer info;
	public EntityEnderDragon entity;
	public String message;

	public boolean display;
	public World spawnWorld;
	private int ticks;

	private int deley;
	
	private float health;

	public BossBar(GunPlayer player){
		info=player;
		spawnWorld=player.getLocation().getWorld();
		deley=10;
		display=false;
		health=300.0F;
	}

	/**
	 * 新規にBossBarをセットする
	 * @param message
	 */
	public void setNewBossBar(String message){
		this.deadEntity(); //前のドラゴンが存在した場合死なせる
		this.spawnEntity();
		this.setDisplayName(message);
	}

	public void tick(){
		if(this.ticks++%deley==0){
			if(!this.isDisplay()){
				this.dead();
			}
			this.teleport();

			World playWorld=info.getLocation().getWorld();
			//ワールドが変更された場合
			//消す -> 再スポーン
			if(!playWorld.getName().equals(spawnWorld.getName())){
				if(this.isDisplay()){
					this.setNewBossBar(getDisplay());
				}
			}
		}
	}
	
	
	private Block getTargetedBlock(int range) {
		Block block = null;
		Location start = info.getBukkitPlayer().getEyeLocation();
		for (int i = 0; i <= range; i++) {
			Location point = start.add(start.getDirection());
			block = point.getBlock();
		}
		return block;
	}

	private void deadEntity(){
		if(this.entity==null)return;
		PacketUtil.sendEntityDestroy(info.getBukkitPlayer(),this.getEntity().getId()); //消す
	}

	
	//teleport出来ないっぽい
	private void teleport(){
		if(this.entity==null)return;
		this.setNewBossBar(getDisplay());
	}

	private void spawnEntity(){

		Location loc=this.getTargetedBlock(85).getLocation();
		WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();

		entity=new EntityEnderDragon(world);
		entity.setInvisible(true);
		entity.setLocation(loc.getX(), loc.getY()-145, loc.getZ(), 0, 0);
		entity.setHealth(this.health);
		entity.setCustomName(this.getDisplay());

		PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(entity);
		PacketUtil.sendPacket(info, packet);

		//関係ないPlayerは消す
		//消す
		this.display=true;
	}

	/**
	 * BossBarのメッセージを変更する
	 * @param dis
	 */
	public void setDisplayName(String dis){
		this.message=dis;
		if(this.getEntity()==null){
			this.spawnEntity();
		}
		entity.setCustomName(dis);
	}
	
	public void setGauge(int level){
		this.health=level*30;
		this.getEntity().setHealth(this.health);
	}

	/**
	 * 非表示
	 */
	public void dead(){
		this.display=false;
		this.deadEntity();
	}

	/**
	 * EntityEnderentity(NMS)を返します
	 * @return
	 */
	public EntityEnderDragon getEntity(){
		return this.entity;
	}

	/**
	 * 表示中であるかを返す
	 * @return
	 */
	public boolean isDisplay(){
		return this.display;
	}

	public String getDisplay(){
		return this.message;
	}

}
