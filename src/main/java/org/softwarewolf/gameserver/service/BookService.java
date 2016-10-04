package org.softwarewolf.gameserver.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.softwarewolf.gameserver.domain.Book;
import org.softwarewolf.gameserver.domain.Folio;
import org.softwarewolf.gameserver.domain.SimpleTag;
import org.softwarewolf.gameserver.domain.dto.BookDto;
import org.softwarewolf.gameserver.repository.BookRepository;
import org.softwarewolf.gameserver.repository.FolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService implements Serializable {
	
	@Autowired
	private BookRepository bookRepository;
	
	@Autowired
	private FolioRepository folioRepository;

	
	private static final long serialVersionUID = 1L;

	public Book save(Book book) throws Exception {
		String errorList = validateBook(book);
		if (errorList.length() > 0) {
			throw new Exception(errorList);
		}
		return bookRepository.save(book);
	}
	
	public String validateBook(Book book) {
		return "";
	}
	
	public List<Book> findAll() {
		return bookRepository.findAll();
	}
	
	public void initBookCreator(BookDto bookCreator, String bookId, String campaignId) {
		Book book = bookRepository.findOne(bookId);
		initBookCreator(bookCreator, book, campaignId);
	}
	
	public void initBookCreator(BookDto bookCreator, Book book, String campaignId) {

	}
	
	public void deleteAll() {
		folioRepository.deleteAll();
	}
	
	public Folio removeTagFromFolio(String folioId, String tagId) {
		Folio folio = folioRepository.findOne(folioId);
		if (folio != null) {
			folio.removeTag(tagId);
		}
		return folioRepository.save(folio);
	}

	public Book addFolioToBook(String folioId, String bookId) {
		Book book = bookRepository.findOne(bookId);
		if (book == null) {
			return null;
		}
		Folio folio = folioRepository.findOne(folioId);
		if (folio == null) {
			return null;
		}
		book.addFolio(folio);
		
		return book;
	}
	
	public Book findOne(String id) {
		return bookRepository.findOne(id);
	}
	
	
	public List<Book> findBooksByTags(List<SimpleTag> selectedTags) {
		List<Object> tagNames = new ArrayList<>();
		for (SimpleTag tag : selectedTags) {
			tagNames.add(tag.getName());
		}
		List<Book> bookList = bookRepository.findAllByKeyValues("folios.tags.name", tagNames.toArray());
		return bookList;
	}
}
