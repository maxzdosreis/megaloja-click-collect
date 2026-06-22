export interface CreateOrderItemRequest {
  productId: number;
  orderId: number;
  quantity: number;
  unitPrice: number;
}
