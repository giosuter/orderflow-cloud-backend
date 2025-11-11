# Angular Front-End Client for OrderFlow API

## Overview
This document provides a minimal Angular front-end implementation for interacting with the OrderFlow API.

## 1. Angular Service (order.service.ts)
```ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Order {
  id?: number;
  code: string;
  status: string;
  total: number;
  createdAt?: string;
  updatedAt?: string;
}

@Injectable({ providedIn: 'root' })
export class OrderService {
  private baseUrl = 'http://localhost:8080/orderflow-api/api/orders';

  constructor(private http: HttpClient) {}

  findAll(): Observable<Order[]> {
    return this.http.get<Order[]>(this.baseUrl);
  }

  findById(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.baseUrl}/${id}`);
  }

  create(order: Order): Observable<Order> {
    return this.http.post<Order>(this.baseUrl, order);
  }

  update(id: number, order: Order): Observable<Order> {
    return this.http.put<Order>(`${this.baseUrl}/${id}`, order);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
```

## 2. Angular Component (order-list.component.ts)
```ts
import { Component, OnInit } from '@angular/core';
import { Order, OrderService } from './order.service';

@Component({
  selector: 'app-order-list',
  templateUrl: './order-list.component.html'
})
export class OrderListComponent implements OnInit {
  orders: Order[] = [];

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.orderService.findAll().subscribe(res => this.orders = res);
  }
}
```

## 3. HTML Template (order-list.component.html)
```html
<h2>Orders</h2>

<table border="1">
  <thead>
    <tr>
      <th>ID</th>
      <th>Code</th>
      <th>Status</th>
      <th>Total</th>
    </tr>
  </thead>
  <tbody>
    <tr *ngFor="let o of orders">
      <td>{{ o.id }}</td>
      <td>{{ o.code }}</td>
      <td>{{ o.status }}</td>
      <td>{{ o.total }}</td>
    </tr>
  </tbody>
</table>
```

## 4. App Module Additions
Ensure `HttpClientModule` is imported in your Angular root module.

```ts
import { HttpClientModule } from '@angular/common/http';

@NgModule({
  declarations: [AppComponent, OrderListComponent],
  imports: [BrowserModule, HttpClientModule],
  bootstrap: [AppComponent]
})
export class AppModule {}
```

## 5. Summary
This minimal client allows:
- Listing Orders
- Extending for Create/Update/Delete
- Integration with the OrderFlow API

You can expand this into a full Angular Material UI later.
