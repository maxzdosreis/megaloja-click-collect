export interface OrderResponse {
  id: number;
  customerId: number;
  customerName: string;
  storeId: number;
  storeName: string;
  status: string;
  pickupCode: string;
  totalAmount: number;
  pickupDeadline: string;
  createdAt: string;
  updatedAt: string;
  items: unknown[];
}
