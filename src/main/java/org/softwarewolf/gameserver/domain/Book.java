package org.softwarewolf.gameserver.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Book implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	private String id;
	private String title;
	private String campaignId;
	private List<Folio> folios;
	
	public Book() { }
	public Book(String campaignId) {
		this.campaignId = campaignId;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getCampaignId() {
		return campaignId;
	}
	
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
	
	public List<Folio> getFolios() {
		if (folios == null) {
			folios = new ArrayList<>();
		}
		return folios;
	}
	
	public void setFolios(List<Folio> folios) {
		this.folios = folios;
	}

	public void addFolio(Folio folio) {
		if (folios == null) {
			folios = new ArrayList<>();
		}
		if (!folios.contains(folio)) {
			folios.add(folio);
		}
	}

	public void removeFolio(Folio folio) {
		if (folios != null && folios.contains(folio)) {
			folios.remove(folio);
		}
	}

}

