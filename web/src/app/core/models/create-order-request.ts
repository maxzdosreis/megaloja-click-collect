import { CreateOrderItemRequest } from './create-order-item-request';

export interface CreateOrderRequest {
  customerId: number;
  storeId: number;
  items: CreateOrderItemRequest[];
}
