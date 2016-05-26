public boolean addDamage(Entity e, double damage){
		//headshotプラスダメージ
		if(damage<=0.0)return false;
		if(e instanceof LivingEntity){
			if(e instanceof Player){
				GunPlayer player=Guns.getPlayerManager().getPlayer((Player) e);
				if(this.shooter==null)return false;
				if(this.shooter.getPlayer().getName().equals(e.getName()))return false;
				if(player.getPlayer().getGameMode()==GameMode.SPECTATOR||player.getPlayer().getGameMode()==GameMode.CREATIVE)return false;
				if(this.shooter.team.equals(player.team))return false;
				if(player.isSpectetor())return false;
				if(player.muteki)return false;

				if(((LivingEntity) e).getHealth() == 0.0D)return true;

				//ダメージを与える
				double heart = ((Damageable) e).getHealth()-(this.damage);
				if(heart > 0.0D){
					//自作イベント呼び出し
					GunEntityDamageEvent event = new GunEntityDamageEvent(this, e, GunDamageCause.SHOT);
					Bukkit.getPluginManager().callEvent(event);
					if(event.isCancelled())return false;
					((Damageable) e).setHealth(heart);
					((LivingEntity) e).damage(0.0D); //仮想のダメージを発生させる

					if(this.shootGun.type==GunType.FIRE){
						e.setFireTicks(60);
					}
				}
				if(heart < 0.1D){
					GunEntityDeathEvent event = new GunEntityDeathEvent(this, e);
					Bukkit.getPluginManager().callEvent(event);
					((LivingEntity) e).damage(20.0D);
				}



			}
		}
		return false;
	}


for(Entity e:loc.getWorld().getEntities()){
			if(e.getLocation().distanceSquared(loc) <= 0.64 || e.getLocation().add(0,1.0,0).distanceSquared(loc)<= 0.9){
				return !this.addDamage(e, damage);
			}

		}
