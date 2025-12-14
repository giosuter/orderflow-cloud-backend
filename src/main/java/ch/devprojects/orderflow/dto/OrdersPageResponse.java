package ch.devprojects.orderflow.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Response wrapper for paginated order search results.
 *
 * Important: - content is List<OrderResponseDto> (not entities) - "content"
 * naming matches Page-like semantics and your current MVC tests
 */
public class OrdersPageResponse {

	private List<OrderResponseDto> content = new ArrayList<>();

	private int page;
	private int size;

	private long totalElements;
	private int totalPages;

	private boolean first;
	private boolean last;

	public OrdersPageResponse() {
		// default constructor for JSON serialization
	}

	public OrdersPageResponse(List<OrderResponseDto> content, int page, int size, long totalElements, int totalPages,
			boolean first, boolean last) {
		this.content = content;
		this.page = page;
		this.size = size;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
		this.first = first;
		this.last = last;
	}

	public List<OrderResponseDto> getContent() {
		return content;
	}

	public void setContent(List<OrderResponseDto> content) {
		this.content = content;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}
}