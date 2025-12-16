package ch.devprojects.orderflow.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Paged response for the "/api/orders/search" endpoint.
 *
 * Why a custom wrapper instead of returning Page<OrderDto> directly? - Keeps
 * the REST contract stable (frontend doesn't depend on Spring's Page JSON
 * shape) - Allows you to add extra metadata later without breaking clients
 */
public class OrdersPageResponse {

	private List<OrderDto> content = new ArrayList<>();
	private int page;
	private int size;
	private long totalElements;
	private int totalPages;

	public OrdersPageResponse() {
	}

	public List<OrderDto> getContent() {
		return content;
	}

	public void setContent(List<OrderDto> content) {
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