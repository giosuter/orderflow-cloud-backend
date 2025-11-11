package ch.devprojects.orderflow.mapper;

import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.dto.OrderDto;

/*
 * DTO uses strings for frontend compatibility.
 * Entity uses enums for safer persistence.
 */
public final class OrderMapper {
    private OrderMapper() {}

    public static OrderDto toDto(Order e) {
        OrderDto d = new OrderDto();
        d.setId(e.getId());
        d.setCode(e.getCode());
        d.setTotal(e.getTotal());
     // convert enum to string
        d.setStatus(e.getStatus().name());  
        d.setCreatedAt(e.getCreatedAt());
        d.setUpdatedAt(e.getUpdatedAt());
        return d;
    }

    public static Order toEntity(OrderDto d) {
        Order e = new Order();
        e.setId(d.getId());
        e.setCode(d.getCode());
        e.setTotal(d.getTotal());
        // convert string to enum
        e.setStatus(Order.Status.valueOf(d.getStatus()));
        e.setCreatedAt(d.getCreatedAt());
        e.setUpdatedAt(d.getUpdatedAt());
        return e;
    }

    public static void copyForUpdate(Order e, OrderDto d) {
        e.setTotal(d.getTotal());
        // string to enum
        e.setStatus(Order.Status.valueOf(d.getStatus()));
    }
}