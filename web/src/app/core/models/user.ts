export interface User {
  id: number;
  name: string;
  email: string;
  role: 'CUSTOMER' | 'EMPLOYEE' | 'MANAGER';
}
