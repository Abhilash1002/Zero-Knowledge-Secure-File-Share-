import { get, post } from "../utils/http";

interface UploadPayload {
  fileName: string;
  ownerEmail: string;
  key: string;
  iv: string;
  fileData: string;
}

export interface FileInfo {
  fileId: number;
  fileName: string;
  key: string;
  iv: string;
}

export interface FileData {
  fileName: string;
  key: string;
  iv: string;
  fileData: string;
}

export interface ShareFilePayload {
  fileId: number;
  senderEmail: string;
  receiverEmail: string;
  key: string;
  iv: string;
  expiry: string; // The expiry date of the file share in DD/MM/YYYY format.
}

export interface FileShareResponse {
  shareId: number;
  fileId: number;
  senderEmail: string;
  receiverEmail: string;
  expiry: string;
}

export interface FileDeleteRequest {
  fileId: number;
}

export async function uploadFile(payload: UploadPayload): Promise<any> {
  return post("/scs-backend/api/v1/send-file", payload);
}

export async function retrieveFile(fileId: number): Promise<any> {
  return get(`/scs-backend/api/v1/get-file?fileId=${fileId}`);
}

export async function retrieveMyFileList(email: string): Promise<any> {
  return get(`/scs-backend/api/v1/my-files?email=${email}`);
}

export async function retrieveSharedUsers(fileId: number): Promise<any> {
  return get(`/scs-backend/api/v1/shared-users-by-file?fileId=${fileId}`);
}

export async function addUserToFileShare(data: ShareFilePayload): Promise<any> {
  return post(`/scs-backend/api/v1/share`, data);
}

export async function removeUserFromFileShare(shareId: number): Promise<any> {
  return post(`/scs-backend/api/v1/revoke-file-access`, { shareId: shareId });
}

export async function deleteFile(data: FileDeleteRequest): Promise<any> {
  return post(`/scs-backend/api/v1/delete-file`, data);
}

// Shared with Me APIs

export interface SharedFileInfo {
  shareId: number;
  fileId: number;
  fileName: string;
  sender: string;
  receiver: string;
  expiry: string;
}

export async function retrieveFilesSharedWithMe(email: string): Promise<any> {
  return get(`/scs-backend/api/v1/shared-with-me?email=${email}`);
}

export async function retrieveSharedFile(shareId: number): Promise<any> {
  return get(`/scs-backend/api/v1/shared-file-data?shareId=${shareId}`);
}

// Public the files
export interface PublicFileShareRequest {
  ownerEmail: string;
  fileId: number;
  fileName: string;
  fileData: string;
}

export interface PublicFileListResponse {
  fileId: number;
  fileName: string;
  fileOwner: string;
}

export interface PublicFileContent {
  fileId: number;
  fileName: string;
  fileData: string;
}

export interface PublicShare {
  fileId: number;
  privateFileId: number;
  ownerId: number;
  fileName: string;
}

export async function shareFilePublicly(
  payload: PublicFileShareRequest
): Promise<any> {
  return post(`/scs-backend/api/v1/share-file-publicly`, payload);
}

export async function getAllPublicFiles(): Promise<any> {
  return get(`/scs-backend/api/v1/get-public-files-list`);
}

export async function getPublicFile(fileId: number): Promise<any> {
  return get(`/scs-backend/api/v1/get-public-file?fileId=${fileId}`);
}

export async function checkIfPublic(fileId: number): Promise<any> {
  return get(`/scs-backend/api/v1/is-public?fileId=${fileId}`);
}

export async function deletePublicFile(fileId: number): Promise<any> {
  return post(`/scs-backend/api/v1/remove-public-file`, { fileId: fileId });
}
