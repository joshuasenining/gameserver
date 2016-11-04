package org.softwarewolf.gameserver.domain.dto;

import org.softwarewolf.gameserver.domain.Folio;

public class ViewFolioDto {
	private Folio folio;
	private String tags;
	private String forwardingUrl;
	
	public Folio getFolio() {
		return folio;
	}
	public void setFolio(Folio folio) {
		this.folio = folio;
	}

	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	
	public String getForwardingUrl() {
		return forwardingUrl;
	}
	public void setForwardingUrl(String forwardingUrl) {
		this.forwardingUrl = forwardingUrl;
	}
}
