package org.softwarewolf.gameserver.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document
public class Campaign implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String name;
	private String campaignFolioId;
	private List<String> ownerList;
	private List<String> gameMasterList;
	private List<String> playerList;
	
	public Campaign() {}
	
	public Campaign(String ownerId) {
		addOwner(ownerId);
	}
	
	public String getId() {
		return id;
	}	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCampaignFolioId() {
		return campaignFolioId;
	}
	public void setCampaignFolioId(String campaignFolioId) {
		this.campaignFolioId = campaignFolioId;
	}
		
	public List<String> getOwnerList() {
		if (ownerList == null) {
			ownerList = new ArrayList<>();
		}
		return ownerList;
	}	
	public void setOwnerList(List<String> ownerList) {
		this.ownerList = ownerList;
	}
	public void addOwner(String ownerId) {
		if (ownerList == null) {
			ownerList = new ArrayList<>();
			ownerList.add(ownerId); 
		} else if (!ownerList.contains(ownerId)) {
			ownerList.add(ownerId);
		}
	}
	public void removeOwner(String ownerId) {
		if (ownerList != null) {
			if (ownerList.contains(ownerId)) {
				ownerList.remove(ownerId);
			}
		}
	}
	
	public List<String> getGameMasterList() {
		if (gameMasterList == null) {
			gameMasterList = new ArrayList<>();
		}
		return gameMasterList;
	}
	public void setGameMasterList(List<String> gameMasterList) {
		this.gameMasterList = gameMasterList;
	}		
	public void addGameMaster(String gameMasterId) {
		if (gameMasterList == null) {
			gameMasterList = new ArrayList<>();
			gameMasterList.add(gameMasterId);
		} else if (!gameMasterList.contains(gameMasterId)) {
			gameMasterList.add(gameMasterId);
		}
	}
	public void removeGameMaster(String gameMasterId) {
		if (gameMasterList != null) {
			if (gameMasterList.contains(gameMasterId)) {
				gameMasterList.remove(gameMasterId);
			}
		}
	}
	
	public List<String> getPlayerList() {
		if (playerList == null) {
			playerList = new ArrayList<>();
		}
		return playerList;
	}	
	public void setPlayerList(List<String> playerList) {
		this.playerList = playerList;
	}
	public void addPlayer(String playerId) {
		if (playerList == null) {
			playerList = new ArrayList<>();
			playerList.add(playerId);
		} else if (!playerList.contains(playerId)) {
			playerList.add(playerId);
		}
	}
	public void removePlayer(String playerId) {
		if (playerList != null) {
			if (playerList.contains(playerId)) {
				playerList.remove(playerId);
			}
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((campaignFolioId == null) ? 0 : campaignFolioId.hashCode());
		result = prime * result + ((gameMasterList == null) ? 0 : gameMasterList.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((ownerList == null) ? 0 : ownerList.hashCode());
		result = prime * result + ((playerList == null) ? 0 : playerList.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Campaign other = (Campaign) obj;
		if (campaignFolioId == null) {
			if (other.campaignFolioId != null)
				return false;
		} else if (!campaignFolioId.equals(other.campaignFolioId))
			return false;
		if (gameMasterList == null) {
			if (other.gameMasterList != null)
				return false;
		} else if (!gameMasterList.equals(other.gameMasterList))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (ownerList == null) {
			if (other.ownerList != null)
				return false;
		} else if (!ownerList.equals(other.ownerList))
			return false;
		if (playerList == null) {
			if (other.playerList != null)
				return false;
		} else if (!playerList.equals(other.playerList))
			return false;
		return true;
	}

	@JsonIgnore
	public ItemSelector getItemSelector() {
		return new ItemSelector(name, id);
	}
}
