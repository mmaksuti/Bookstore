package main;
import java.io.Serializable;
import java.util.ArrayList;

public class Book implements Serializable {
	private static final long serialVersionUID = 5296705482940410483L;
	private String isbn13;
	private String title;
	private String description;
	private double price;
	private Author author;
	private ArrayList<Genre> genres = new ArrayList<>();
	private int quantity;
	private boolean paperback; // or e-book
	
	public Book(String isbn13, String title, String description, double price, Author author, ArrayList<Genre> genres, int quantity, boolean paperback) {
		this.isbn13 = isbn13;
		this.title = title;
		this.price = price;
		this.author = author;
		this.description = description;
		this.paperback = paperback;
		this.quantity = quantity;
		this.genres = genres;
	}
    
	public String getIsbn13() {
		return isbn13;
	}

	public void setIsbn13(String isbn13) {
		this.isbn13 = isbn13;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public ArrayList<Genre> getGenres() {
		return genres;
	}

	public void setGenres(ArrayList<Genre> genres) {
		this.genres = genres;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isPaperback() {
		return paperback;
	}

	public void setPaperback(boolean paperback) {
		this.paperback = paperback;
	}
	
	@Override
	public String toString() {
		return this.title + " by " + this.author.toString() + ", " + this.price + " leke";
	}
}
