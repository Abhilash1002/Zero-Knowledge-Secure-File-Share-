import CryptoJS from "crypto-js";

export async function generateKeyPair() {
  const keyPair = await window.crypto.subtle.generateKey(
    {
      name: "RSA-OAEP",
      modulusLength: 2048, // Length of the key in bits (2048 is common)
      publicExponent: new Uint8Array([1, 0, 1]), // 65537
      hash: "SHA-256", // Hash function to use
    },
    true, // Whether the key is extractable (i.e. can be exported)
    ["encrypt", "decrypt"] // Usage of the key
  );

  return keyPair;
}

export const arrayBufferToBase64 = (buffer: ArrayBuffer) => {
  const binary = String.fromCharCode(...new Uint8Array(buffer));
  return btoa(binary);
};

export const base64ToArrayBuffer = (base64: string) => {
  const binary = atob(base64);
  const len = binary.length;
  const bytes = new Uint8Array(len);
  for (let i = 0; i < len; i++) {
    bytes[i] = binary.charCodeAt(i);
  }
  return bytes.buffer;
};

export async function exportPublicKey(publicKey: CryptoKey): Promise<string> {
  const exported = await window.crypto.subtle.exportKey("spki", publicKey);

  return arrayBufferToBase64(exported);
}

export async function exportPrivateKey(privateKey: CryptoKey): Promise<string> {
  const exported = await window.crypto.subtle.exportKey("pkcs8", privateKey);

  return arrayBufferToBase64(exported);
}

// Constants for AES
const AES_KEY_SIZE = 256; // bits
const AES_IV_SIZE = 16; // bytes

export function generateFileKey(): string {
  return CryptoJS.lib.WordArray.random(AES_KEY_SIZE / 8).toString();
}

export function generateIV(): string {
  return CryptoJS.lib.WordArray.random(AES_IV_SIZE).toString();
}

export function generateFileKeyAndIV(): { fileKey: string; iv: string } {
  return { fileKey: generateFileKey(), iv: generateIV() };
}

export async function encryptWithPublicKey(
  publicKey: string,
  data: string
): Promise<string> {
  const publicKeyBuffer = str2ab(atob(publicKey));
  const importedPublicKey = await window.crypto.subtle.importKey(
    "spki",
    publicKeyBuffer,
    {
      name: "RSA-OAEP",
      hash: "SHA-256",
    },
    false,
    ["encrypt"]
  );

  const encodedData = new TextEncoder().encode(data);
  const encryptedBuffer = await window.crypto.subtle.encrypt(
    {
      name: "RSA-OAEP",
    },
    importedPublicKey,
    encodedData
  );

  return btoa(String.fromCharCode(...new Uint8Array(encryptedBuffer)));
}

export async function decryptWithPrivateKey(
  privateKey: string,
  encryptedData: string
): Promise<string> {
  const privateKeyBuffer = str2ab(atob(privateKey));
  const importedPrivateKey = await window.crypto.subtle.importKey(
    "pkcs8",
    privateKeyBuffer,
    {
      name: "RSA-OAEP",
      hash: "SHA-256",
    },
    false,
    ["decrypt"]
  );

  const encryptedBuffer = str2ab(atob(encryptedData));
  const decryptedBuffer = await window.crypto.subtle.decrypt(
    {
      name: "RSA-OAEP",
    },
    importedPrivateKey,
    encryptedBuffer
  );

  return new TextDecoder().decode(decryptedBuffer);
}

export function aesEncryptFile(
  file: File,
  fileKey: string,
  iv: string
): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = function (e: ProgressEvent<FileReader>) {
      if (e.target && e.target.result) {
        const wordArray = CryptoJS.lib.WordArray.create(
          e.target.result as ArrayBuffer
        );
        const encrypted = CryptoJS.AES.encrypt(
          wordArray,
          CryptoJS.enc.Hex.parse(fileKey),
          {
            iv: CryptoJS.enc.Hex.parse(iv),
            mode: CryptoJS.mode.CBC,
            padding: CryptoJS.pad.Pkcs7,
          }
        ).toString();
        resolve(encrypted);
      } else {
        reject(new Error("Failed to read file"));
      }
    };
    reader.onerror = function (e: ProgressEvent<FileReader>) {
      reject(e);
    };
    reader.readAsArrayBuffer(file);
  });
}

export function aesDecryptFile(
  encryptedData: string,
  fileKey: string,
  iv: string
): Uint8Array {
  const decryptedWordArray = CryptoJS.AES.decrypt(
    encryptedData,
    CryptoJS.enc.Hex.parse(fileKey),
    {
      iv: CryptoJS.enc.Hex.parse(iv),
      mode: CryptoJS.mode.CBC,
      padding: CryptoJS.pad.Pkcs7,
    }
  );
  return convertWordArrayToUint8Array(decryptedWordArray);
}

function convertWordArrayToUint8Array(
  wordArray: CryptoJS.lib.WordArray
): Uint8Array {
  const arrayOfWords = wordArray.words || [];
  const length = wordArray.sigBytes || arrayOfWords.length * 4;
  const uInt8Array = new Uint8Array(length);
  let index = 0;
  for (let i = 0; i < length; i += 4) {
    const word = arrayOfWords[i / 4];
    uInt8Array[index++] = (word >> 24) & 0xff;
    uInt8Array[index++] = (word >> 16) & 0xff;
    uInt8Array[index++] = (word >> 8) & 0xff;
    uInt8Array[index++] = word & 0xff;
  }
  return uInt8Array;
}

// Helper function to convert string to ArrayBuffer
function str2ab(str: string): ArrayBuffer {
  const buf = new ArrayBuffer(str.length);
  const bufView = new Uint8Array(buf);
  for (let i = 0, strLen = str.length; i < strLen; i++) {
    bufView[i] = str.charCodeAt(i);
  }
  return buf;
}

// Helper function to read a file as text
export const readFileAsText = (file: File): Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = (event) => resolve(event.target?.result as string);
    reader.onerror = (error) => reject(error);
    reader.readAsText(file);
  });
};

export const readFileAsBase64 = (file: File): Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => {
      const result = reader.result as string;
      // Remove the MIME type prefix and only send the base64 data
      const base64Data = result.split(",")[1];
      resolve(base64Data);
    };
    reader.onerror = (error) => reject(error);
    reader.readAsDataURL(file);
  });
};

export const arrayBufferToBase64ForPublic = (buffer: ArrayBuffer): string => {
  let binary = "";
  const bytes = new Uint8Array(buffer);
  const len = bytes.byteLength;
  for (let i = 0; i < len; i++) {
    binary += String.fromCharCode(bytes[i]);
  }
  return window.btoa(binary);
};
