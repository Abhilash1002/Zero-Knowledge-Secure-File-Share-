import { get, post } from "../utils/http";

export interface UpdateKeyRequest {
  email: string;
  publicKey: string;
}

export async function updatePublicKey(data: UpdateKeyRequest): Promise<any> {
  return post("/scs-backend/api/v1/user/public-key", data);
}

export async function checkUserExistence(identifier: string): Promise<any> {
  return get(`/scs-backend/api/v1/user/exists?identifier=${identifier}`);
}

export async function getUserPublicKey(identifier: string): Promise<any> {
  return get(`/scs-backend/api/v1/get-receiver-details?email=${identifier}`);
}
