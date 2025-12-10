package ch.devprojects.orderflow.dto;

import java.util.List;

/**
 * Simple paging wrapper for OrderResponseDto instances.
 *
 * We use this instead of Spring's Page<> type to: - keep the JSON response
 * stable, - decouple the API contract from Spring Data internals, - have a
 * clear, interview-friendly structure.
 */
public class OrdersPageResponse {

	// Actual slice of orders for the current page
	private List<OrderResponseDto> content;

	// 0-based page index requested by the client
	private int page;

	// Page size requested by the client
	private int size;

	// Total number of orders matching the filter (across all pages)
	private long totalElements;

	// Total number of pages (derived from totalElements and size)
	private int totalPages;

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
}