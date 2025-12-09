package ch.devprojects.orderflow.analytics.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for basic high-level analytics.
 *
 * Contains: - total number of orders - count of open / completed / cancelled
 * orders - summed revenue - average order value - generatedAt: timestamp of
 * when analytics were calculated
 */
public class AnalyticsOverviewDto {

	private long totalOrders;
	private long openOrders;
	private long completedOrders;
	private long cancelledOrders;

	private BigDecimal totalRevenue;
	private BigDecimal averageOrderValue;

	private Instant generatedAt; // NEW FIELD

	public long getTotalOrders() {
		return totalOrders;
	}

	public void setTotalOrders(long totalOrders) {
		this.totalOrders = totalOrders;
	}

	public long getOpenOrders() {
		return openOrders;
	}

	public void setOpenOrders(long openOrders) {
		this.openOrders = openOrders;
	}

	public long getCompletedOrders() {
		return completedOrders;
	}

	public void setCompletedOrders(long completedOrders) {
		this.completedOrders = completedOrders;
	}

	public long getCancelledOrders() {
		return cancelledOrders;
	}

	public void setCancelledOrders(long cancelledOrders) {
		this.cancelledOrders = cancelledOrders;
	}

	public BigDecimal getTotalRevenue() {
		return totalRevenue;
	}

	public void setTotalRevenue(BigDecimal totalRevenue) {
		this.totalRevenue = totalRevenue;
	}

	public BigDecimal getAverageOrderValue() {
		return averageOrderValue;
	}

	public void setAverageOrderValue(BigDecimal averageOrderValue) {
		this.averageOrderValue = averageOrderValue;
	}

	public Instant getGeneratedAt() {
		return generatedAt;
	}

	public void setGeneratedAt(Instant generatedAt) {
		this.generatedAt = generatedAt;
	}
}