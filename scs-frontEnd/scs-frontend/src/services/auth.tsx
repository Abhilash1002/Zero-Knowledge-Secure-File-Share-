import { preloginPost } from "../utils/http";

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  id: string;
  email: string;
  token: string;
  publicKey: string | null;
}

export interface RegisterRequest {
  userName: string;
  email: string;
  password: string;
}

export interface RegisterResponse {
  id: string;
  email: string;
  msg: string;
}

export interface User {
  id: string;
  email: string;
  userName: string;
  publicKey: string | null;
}

export const getUser = (): User => {
  return JSON.parse(sessionStorage.getItem("user") || "{}") as User;
};

export async function login(info: LoginRequest): Promise<any> {
  return preloginPost("/scs-backend/api/v1/auth/login", info);
}

export async function register(info: RegisterRequest): Promise<any> {
  return preloginPost("/scs-backend/api/v1/auth/register", info);
}
