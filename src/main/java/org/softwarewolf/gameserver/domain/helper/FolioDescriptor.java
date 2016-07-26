package org.softwarewolf.gameserver.domain.helper;

import java.util.List;

import org.softwarewolf.gameserver.domain.SimpleTag;

/**
 * Folios can be assembled into collections for multiple purposes. This abbreviated object
 * is for use when creating those collections. A folio knows how to create a descriptor.
 * @author tmanchester
 *
 */
public class FolioDescriptor {
	String folioTitle;
	String folioId;
	List<SimpleTag> tags;
	
	public FolioDescriptor(String folioTitle, String folioId, List<SimpleTag> tags) {
		this.folioTitle = folioTitle;
		this.folioId = folioId;
		this.tags = tags;
	}
	
	public String getFolioTitle() {
		return folioTitle;
	}
	
	public void setFolioTitle(String folioTitle) {
		this.folioTitle = folioTitle;
	}
	
	public String getFolioId() {
		return folioId;
	}
	
	public void setFolioId(String folioId) {
		this.folioId = folioId;
	}
	
	public List<SimpleTag> getTags() {
		return tags;
	}
	
	public void setTags(List<SimpleTag> tags) {
		this.tags = tags;
	}
	
	public void addTag(SimpleTag tag) {
		if (!tags.contains(tag)) {
			tags.add(tag);
		}
	}
}