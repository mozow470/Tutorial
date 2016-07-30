package com.github.mozow470.guns.utils;

import java.lang.reflect.Field;

import net.minecraft.server.v1_8_R3.EntityCreeper;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityZombie;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity.PacketPlayOutEntityLook;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.WorldServer;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import com.github.mozow470.guns.api.DaisukeAPI;
import com.github.mozow470.guns.packet.PacketUtil;
import com.github.mozow470.guns.support.GunPlayer;

public class Disguise {

	private GunPlayer owner;
	private EntityLiving el;
	private Location last;

	public Disguise(GunPlayer player){
		this.owner=player;
		this.last=player.getLocation();
	}

	public void tick(){
		if(this.getDisguiseEntity()!=null){
				Location loc=this.owner.getLocation();
				this.move(this.last,loc);
				this.last=loc;
				this.getDisguiseEntity().setSneaking(this.owner.getBukkitPlayer().isSneaking());
		}
	}

	private void setMob(DisguiseType type){
		this.owner.getBukkitPlayer().setDisplayName(this.owner.getBukkitName());

		EntityLiving entity = this.getEntity(type);
		this.el=entity;
		Location loc=this.owner.getLocation();

		this.getDisguiseEntity().setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
		PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(entity);

		for(GunPlayer other:DaisukeAPI.getOnlinePlayerInfo()){
			if(other!=this.owner.getBukkitPlayer()){
				PacketUtil.sendEntityDestroy(other.getBukkitPlayer(), this.owner.getBukkitPlayer().getEntityId()); //消す
			}
			PacketUtil.sendPacket(other, packet); //NPCを表示
		}
	}

	@SuppressWarnings("rawtypes")
	private void setPrivateField(Class type, Object object, String name, Object value) {
		try {
			Field f = type.getDeclaredField(name);
			f.setAccessible(true);
			f.set(object, value);
			f.setAccessible(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void move( Location old, Location newLoc ){
		
		PacketPlayOutRelEntityMoveLook packet = new PacketPlayOutRelEntityMoveLook(this.getDisguiseEntity().getId(),
		(byte) ((newLoc.getBlockX() - last.getBlockX()) * 32), // Fixed Point Format
		(byte) ((newLoc.getBlockY() - last.getBlockY()) * 32), // Fixed Point Format
		(byte) ((newLoc.getBlockZ() - last.getBlockZ()) * 32), // Fixed Point Format
		(byte)0, // Site says "fraction of 360", might need tweaking
		(byte)0, true); // Site says "fraction of 360", might need tweaking
		
		PacketPlayOutEntityLook body = new PacketPlayOutEntityLook(this.getDisguiseEntity().getId(),(byte) ((newLoc.getYaw() + 180) / 360), (byte) ((newLoc.getPitch() + 180) / 360), false);
		PacketPlayOutEntityHeadRotation head = new PacketPlayOutEntityHeadRotation();
		setPrivateField(PacketPlayOutEntityHeadRotation.class, head, "a", this.getDisguiseEntity().getId());
		setPrivateField(PacketPlayOutEntityHeadRotation.class, head, "b", getCompressedAngle(newLoc.getYaw()));
		
		for(GunPlayer other:DaisukeAPI.getOnlinePlayerInfo()){
//			if(other!=this.owner){
				PacketUtil.sendPacket(other, packet); //動き
				PacketUtil.sendPacket(other, head); //顔の向き
				PacketUtil.sendPacket(other, body); //体
//			}
		}
	}

	private byte getCompressedAngle(float value) {
		return (byte) ((value * 256.0F) / 360.0F);
	}
	


	public void setDisguise(DisguiseType type){
		this.setMob(type);
	}

	public enum DisguiseType{
		ZOMBIE(),VILLAGER(),CREEPER(),WITHER(),ENDAR_DRAGON()
	}

	public EntityLiving getDisguiseEntity(){
		return this.el;
	}


	private EntityLiving getEntity(DisguiseType type){
		switch(type){
		case CREEPER:
			return new EntityCreeper(this.getWorld());
		case ENDAR_DRAGON:
			break;
		case VILLAGER:
			break;
		case WITHER:
			break;
		case ZOMBIE:
			return new EntityZombie(this.getWorld());
		default:
			break;

		}
		return new EntityZombie(this.getWorld());
	}

	private WorldServer getWorld(){
		return  ((CraftWorld) this.owner.getLocation().getWorld()).getHandle();
	}
}
