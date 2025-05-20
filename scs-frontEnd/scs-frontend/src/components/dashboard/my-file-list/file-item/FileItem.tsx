import React, { useEffect, useState } from "react";
import {
  Box,
  Typography,
  IconButton,
  Menu,
  MenuItem,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Badge,
} from "@mui/material";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import {
  deleteFile,
  FileDeleteRequest,
  FileInfo,
  retrieveFile,
  FileData,
  shareFilePublicly,
  PublicFileShareRequest,
  checkIfPublic,
  deletePublicFile,
  PublicShare,
} from "../../../../services/file-service";
import {
  decryptWithPrivateKey,
  aesDecryptFile,
  readFileAsText,
  arrayBufferToBase64ForPublic,
} from "../../../../services/crypto";
import FileShare from "../file-share/FileShare";
import { getUser } from "../../../../services/auth";

interface FileItemProps {
  file: FileInfo;
  onDelete: (fileId: number) => void;
}

const FileItem: React.FC<FileItemProps> = ({ file, onDelete }) => {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [isDownloadDialogOpen, setDownloadDialogOpen] = useState(false);
  const [isDeleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [isShareDialogOpen, setShareDialogOpen] = useState(false);
  const [isPublicDialogOpen, setPublicDialogOpen] = useState(false);
  const [isPublic, setPublic] = useState<PublicShare | null>(null);
  const [privateKey, setPrivateKey] = useState<File | null>(null);

  useEffect(() => {
    const checkPublicStatus = () => {
      checkIfPublic(file.fileId)
        .then((response) => {
          if (!response.ok) {
            throw new Error("Error checking file status");
          }
          return response.json() as unknown as PublicShare;
        })
        .then((isPublic: PublicShare) => {
          setPublic(isPublic);
        })
        .catch((error) => {
          console.error("Error checking file status:", error);
          setPublic(null);
        });
    };

    checkPublicStatus();
  }, [file.fileId]);

  const handleMenuClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleDownload = () => {
    setDownloadDialogOpen(true);
    handleMenuClose();
  };

  const handleShare = () => {
    setShareDialogOpen(true);
    handleMenuClose();
  };

  const handleDelete = () => {
    setDeleteDialogOpen(true);
    handleMenuClose();
  };

  const handleMakePublic = () => {
    setPublicDialogOpen(true);
    handleMenuClose();
  };

  const downloadFile = async (privateKeyFile: File, fileId: number) => {
    try {
      // Retrieve the file data
      const response = await retrieveFile(fileId);
      if (!response.ok) {
        throw new Error("Something went wrong while fetching the file");
      }
      const fileData: FileData = await response.json();

      // Read the private key file
      const privateKey = await readFileAsText(privateKeyFile);

      // Decrypt the file key and IV
      const decryptedFileKey = await decryptWithPrivateKey(
        privateKey,
        fileData.key
      );
      const decryptedIV = await decryptWithPrivateKey(privateKey, fileData.iv);

      // Decrypt the file data
      const decryptedFileData = aesDecryptFile(
        fileData.fileData,
        decryptedFileKey,
        decryptedIV
      );

      // Create a Blob from the decrypted data
      const blob = new Blob([decryptedFileData], {
        type: "application/octet-stream",
      });

      // Create a download link and trigger the download
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.style.display = "none";
      a.href = url;
      a.download = fileData.fileName || "downloaded_file";
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error("Error while decrypting the file");
      throw new Error("Something went wrong while decrypting the file");
    }
  };

  const handleDownloadConfirm = () => {
    if (privateKey) {
      // Perform download action with the private key
      console.log("Downloading with private key:", privateKey.name);

      downloadFile(privateKey, file.fileId);
      setDownloadDialogOpen(false);
      setPrivateKey(null);
    }
  };

  const handleDeleteConfirm = () => {
    console.log("Deleting file:", file.fileName);
    deleteFile({ fileId: file.fileId } as FileDeleteRequest)
      .then((res) => {
        if (!res.ok) throw new Error("Error deleting file");
        onDelete(file.fileId);
        return res;
      })
      .catch((err) => {
        console.error("Failed to delete file:", err);
      })
      .finally(() => {
        setDeleteDialogOpen(false);
      });
  };

  const handleMakePublicConfirm = async () => {
    try {
      // Retrieve the file data
      const user = getUser();
      const response = await retrieveFile(file.fileId);
      if (!response.ok) {
        throw new Error("Something went wrong while fetching the file");
      }
      const fileData: FileData = await response.json();

      if (!privateKey) {
        throw new Error("Please upload your private key");
      }

      // Read the private key file
      const privKey = await readFileAsText(privateKey);

      // Decrypt the file key and IV
      const decryptedFileKey = await decryptWithPrivateKey(
        privKey,
        fileData.key
      );
      const decryptedIV = await decryptWithPrivateKey(privKey, fileData.iv);

      // Decrypt the file data
      const decryptedFileData = await aesDecryptFile(
        fileData.fileData,
        decryptedFileKey,
        decryptedIV
      );

      // Convert decrypted file data to Base64
      const fileDataBase64 = arrayBufferToBase64ForPublic(decryptedFileData);

      // Create JSON payload
      const publicFileData: PublicFileShareRequest = {
        fileId: file.fileId,
        ownerEmail: user.email,
        fileName: file.fileName,
        fileData: fileDataBase64,
      };

      // Send JSON payload to backend

      shareFilePublicly(publicFileData)
        .then((shareResponse) => {
          if (!shareResponse.ok) {
            throw new Error("Failed to share file publicly");
          }
          return shareResponse.json() as unknown as PublicShare;
        })
        .then((res) => {
          setPublic(res);
        });
    } catch (error) {
      console.error("Error while making file public:", error);
    } finally {
      setPublicDialogOpen(false);
      setPrivateKey(null);
    }
  };

  const handlePrivateKeyChange = (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    if (event.target.files && event.target.files.length > 0) {
      setPrivateKey(event.target.files[0]);
    }
  };

  const handleMakePrivate = () => {
    if (!isPublic) {
      return;
    }
    try {
      deletePublicFile(isPublic.fileId)
        .then((res) => {
          if (!res.ok) {
            throw new Error("Error while making file private");
          }
          // If the response is true, the file has been successfully made private
          setPublic(null);
          handleMenuClose();
        })
        .catch((err) => {
          console.error("Failed to make the file private:", err);
        });
    } catch (err) {
      console.error("Error occurred:", err);
    }
  };

  return (
    <Box
      sx={{
        display: "flex",
        alignItems: "center",
        padding: 1,
        borderRadius: 2,
        boxShadow: "0px 2px 4px rgba(0, 0, 0, 0.1)",
        mb: 1,
      }}
    >
      <Box
        sx={{
          width: 40,
          height: 40,
          backgroundColor: "grey.300",
          borderRadius: "50%",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          mr: 2,
        }}
      >
        <Typography variant="caption" color="purple">
          {file.fileName[0].toUpperCase()}
        </Typography>
      </Box>
      <Typography variant="body2" sx={{ flexGrow: 1 }}>
        {file.fileName}
      </Typography>
      {isPublic && (
        <Badge badgeContent="Public" color="success" sx={{ marginRight: 2 }} />
      )}
      <IconButton onClick={handleMenuClick}>
        <MoreVertIcon />
      </IconButton>
      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleMenuClose}
      >
        <MenuItem onClick={handleDownload}>Download</MenuItem>
        <MenuItem onClick={handleShare}>Share</MenuItem>
        {isPublic ? (
          <MenuItem onClick={handleMakePrivate}>Make Private</MenuItem>
        ) : (
          <MenuItem onClick={handleMakePublic}>Make Public</MenuItem>
        )}
        <MenuItem onClick={handleDelete}>Delete</MenuItem>
      </Menu>

      {/* Download Dialog */}
      <Dialog
        open={isDownloadDialogOpen}
        onClose={() => setDownloadDialogOpen(false)}
      >
        <DialogTitle>Upload Private Key</DialogTitle>
        <DialogContent>
          <Typography variant="body2" gutterBottom>
            Please upload your private key file to proceed with the download.
          </Typography>
          <input
            type="file"
            accept=".key,.pem"
            onChange={handlePrivateKeyChange}
            style={{ marginTop: 10 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDownloadDialogOpen(false)}>Cancel</Button>
          <Button
            onClick={handleDownloadConfirm}
            disabled={!privateKey}
            variant="contained"
            color="primary"
          >
            Confirm
          </Button>
        </DialogActions>
      </Dialog>

      {/* Delete Dialog */}
      <Dialog
        open={isDeleteDialogOpen}
        onClose={() => setDeleteDialogOpen(false)}
      >
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <Typography variant="body2">
            Are you sure you want to delete {file.fileName}?
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialogOpen(false)}>Cancel</Button>
          <Button
            onClick={handleDeleteConfirm}
            variant="contained"
            color="secondary"
          >
            Delete
          </Button>
        </DialogActions>
      </Dialog>

      {/* Make Public Dialog */}
      <Dialog
        open={isPublicDialogOpen}
        onClose={() => setPublicDialogOpen(false)}
      >
        <DialogTitle>Make File Public</DialogTitle>
        <DialogContent>
          <input
            type="file"
            accept=".key,.pem"
            onChange={handlePrivateKeyChange}
            style={{ marginTop: 10 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setPublicDialogOpen(false)}>Cancel</Button>
          <Button
            onClick={handleMakePublicConfirm}
            disabled={!privateKey}
            variant="contained"
            color="primary"
          >
            Confirm
          </Button>
        </DialogActions>
      </Dialog>

      <FileShare
        open={isShareDialogOpen}
        onClose={() => setShareDialogOpen(false)}
        file={file}
      />
    </Box>
  );
};

export default FileItem;
