package ch.devprojects.orderflow.domain;

/**
 * Represents the lifecycle status of an order. Each constant describes a
 * distinct step in the processing workflow.
 */
public enum OrderStatus {

	/**
	 * Order was created but not yet processed. Typically the initial state after
	 * order creation.
	 */
	NEW,

	/**
	 * Order is currently being processed (for example: payment, packaging, or
	 * validation). Indicates the system or staff are actively working on the order.
	 */
	PROCESSING,

	/**
	 * Alternative state used by analytics and some frontend views. Represents an
	 * order that is actively being handled.
	 */
	OPEN,

	/**
	 * Order has been successfully paid by the customer. This state confirms the
	 * payment has been received and verified.
	 */
	PAID,

	/**
	 * Order has been shipped to the customer. The order has left the warehouse or
	 * origin and is in transit.
	 */
	SHIPPED,

	/**
	 * Order has been fully processed and is considered done. Used by analytics and
	 * reporting.
	 */
	COMPLETED,

	/**
	 * Order has been cancelled either by the customer or by the system. No further
	 * processing occurs after this state.
	 */
	CANCELLED
}