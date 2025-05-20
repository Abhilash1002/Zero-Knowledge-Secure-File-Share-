import React, { useState } from "react";
import { Button, CircularProgress } from "@mui/material";
import { uploadFile } from "../../../services/file-service";
import { getUser } from "../../../services/auth";
import {
  generateFileKeyAndIV,
  aesEncryptFile,
  encryptWithPublicKey,
} from "../../../services/crypto";

interface FileUploadHandlerProps {
  files: File[];
  onUploadComplete: () => void;
}

interface UploadPayload {
  fileName: string;
  ownerEmail: string;
  key: string;
  iv: string;
  fileData: string;
}

const FileUploadHandler: React.FC<FileUploadHandlerProps> = ({
  files,
  onUploadComplete,
}) => {
  const [isUploading, setIsUploading] = useState(false);

  const handleUpload = async () => {
    setIsUploading(true);
    const user = getUser();
    const publicKey = user.publicKey;

    if (!publicKey) {
      console.error("Public key is not available. Cannot encrypt file.");
      setIsUploading(false);
      return;
    }

    for (const file of files) {
      try {
        // const fileData = await readFileAsBase64(file);

        // Generate fileKey and IV
        const { fileKey, iv } = generateFileKeyAndIV();

        // Encrypt fileData using AES
        const encryptedFileData = await aesEncryptFile(file, fileKey, iv);

        // Encrypt the fileKey and IV with public key
        const encryptedFileKey = await encryptWithPublicKey(publicKey, fileKey);
        const encryptedIV = await encryptWithPublicKey(publicKey, iv);

        const payload: UploadPayload = {
          fileName: file.name,
          ownerEmail: user.email,
          key: encryptedFileKey, // Replace with actual key when implementing encryption
          iv: encryptedIV, // Replace with actual IV when implementing encryption
          fileData: encryptedFileData,
        };

        await uploadFile(payload);
      } catch (error) {
        console.error(`Error uploading file ${file.name}:`, error);
      }
    }

    setIsUploading(false);
    onUploadComplete();
  };

  return (
    <Button
      variant="contained"
      onClick={handleUpload}
      disabled={isUploading || files.length === 0}
    >
      {isUploading ? <CircularProgress size={24} /> : "Upload Files"}
    </Button>
  );
};

export default FileUploadHandler;
