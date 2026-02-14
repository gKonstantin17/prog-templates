export interface Role {
  id: number;
  name: string;
}

export interface User {
  id: number;
  name: string;
  login: string;
  password?: string;
  role: Role;
}
