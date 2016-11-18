package org.softwarewolf.gameserver.domain.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.softwarewolf.gameserver.domain.Campaign;

public class CampaignDto {
	public List<UserListItem> gamemasters;
	public List<UserListItem> players;
	public String ownerId;
	public Campaign campaign;
	
	public List<UserListItem> getGamemasters() {
		return gamemasters;
	}
	
	public void setGamemasters(List<UserListItem> gamemasters) {
		this.gamemasters = gamemasters;
	}
	
	public void addGameMaster(Map<String, String> gamemaster) {
		if (gamemasters == null) {
			gamemasters = new ArrayList<UserListItem>();
		}
		String key = (String)gamemaster.keySet().toArray()[0];
		String value = gamemaster.get(key);
		for (UserListItem item : gamemasters) {
			if (item.id.equals(key)) {
				item.displayName = value;
				return;
			}
		}

		UserListItem newItem = new UserListItem(key, value);
		gamemasters.add(newItem);
	}
	
	public void removeGamemaste(String gamemasterUserId) {
		if (gamemasters != null) {
			gamemasters.remove(gamemasterUserId);
		}
	}
	
	public List<UserListItem> getPlayers() {
		return players;
	}
	
	public void setPlayers(List<UserListItem> players) {
		this.players = players;
	}
	
	public void addPlayer(Map<String, String> player) {
		if (players == null) {
			players = new ArrayList<UserListItem>();
		}
		String key = (String)player.keySet().toArray()[0];
		String value = player.get(key);
		for (UserListItem item : players) {
			if (item.id.equals(key)) {
				item.displayName = value;
				return;
			}
		}

		UserListItem newItem = new UserListItem(key, value);
		players.add(newItem);
	}
	
	public void removePlayer(String playerUserId) {
		if (players != null) {
			players.remove(playerUserId);
		}
	}
	
	public String getOwnerId() {
		return ownerId;
	}
	
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	
	public Campaign getCampaign() {
		return campaign;
	}
	
	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}
}
