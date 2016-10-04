package org.softwarewolf.gameserver.domain.dto;

import java.util.List;

import org.softwarewolf.gameserver.domain.Book;

public class BookDto {
	private Book book;
	private List<Book> bookList;
	private String forwardingUrl;
	
	public BookDto() {
		book = new Book();
	}
	
	public Book getBook() {
		 return book;
	}
	public void setBooko(Book book) {
		this.book = book;
	}
	
	public List<Book> getBookList() {
		return bookList;
	}
	public void setBookList(List<Book> bookList) {
		this.bookList = bookList;
	}

	public String getForwardingUrl() {
		return forwardingUrl;
	}
	public void setForwardingUrl(String forwardingUrl) {
		this.forwardingUrl = forwardingUrl;
	}
}
