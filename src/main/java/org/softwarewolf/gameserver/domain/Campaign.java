package org.softwarewolf.gameserver.domain;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Campaign implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String name;
	private String campaignFolioId;
	private String ownerId;
	private List<String> gameMasterIdList;

	public Campaign() {}
	
	public Campaign(String ownerId) {
		this.ownerId = ownerId;
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
		
	public String getOwnerId() {
		return ownerId;
	}
	
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	
	public List<String> getGameMasterIdList() {
		return gameMasterIdList;
	}
	
	public void setGameMasterIdList(List<String> gameMasterIdList) {
		this.gameMasterIdList = gameMasterIdList;
	}
		
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((campaignFolioId == null) ? 0 : campaignFolioId.hashCode());
		result = prime * result + ((gameMasterIdList == null) ? 0 : gameMasterIdList.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
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
		if (gameMasterIdList == null) {
			if (other.gameMasterIdList != null)
				return false;
		} else if (!gameMasterIdList.equals(other.gameMasterIdList))
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
		if (ownerId == null) {
			if (other.ownerId != null)
				return false;
		} else if (!ownerId.equals(other.ownerId))
			return false;
		return true;
	}	
}
