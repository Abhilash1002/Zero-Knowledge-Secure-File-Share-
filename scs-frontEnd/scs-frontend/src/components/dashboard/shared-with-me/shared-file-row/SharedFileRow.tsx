import React, { useState } from "react";
import {
  Box,
  Typography,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Menu,
  MenuItem,
} from "@mui/material";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import {
  FileData,
  SharedFileInfo,
  retrieveSharedFile,
} from "../../../../services/file-service";
import {
  decryptWithPrivateKey,
  aesDecryptFile,
  readFileAsText,
} from "../../../../services/crypto";

interface SharedFileRowProps {
  file: SharedFileInfo;
}

const SharedFileRow: React.FC<SharedFileRowProps> = ({ file }) => {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [isDownloadDialogOpen, setDownloadDialogOpen] = useState(false);
  const [privateKey, setPrivateKey] = useState<File | null>(null);

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

  const downloadFile = async (privateKeyFile: File, shareId: number) => {
    try {
      const response = await retrieveSharedFile(shareId);
      if (!response.ok) {
        throw new Error("Something went wrong while fetching the file");
      }
      const fileData: FileData = await response.json();

      const privateKey = await readFileAsText(privateKeyFile);

      const decryptedFileKey = await decryptWithPrivateKey(
        privateKey,
        fileData.key
      );
      const decryptedIV = await decryptWithPrivateKey(privateKey, fileData.iv);

      const decryptedFileData = aesDecryptFile(
        fileData.fileData,
        decryptedFileKey,
        decryptedIV
      );

      const blob = new Blob([decryptedFileData], {
        type: "application/octet-stream",
      });

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
      downloadFile(privateKey, file.shareId);
      setDownloadDialogOpen(false);
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
        <Typography variant="caption" color="text.secondary">
          F
        </Typography>
      </Box>
      <Box sx={{ flexGrow: 1 }}>
        <Typography variant="body2">{file.fileName}</Typography>
        <Box sx={{ display: "flex", mt: 0.5, gap: "10px" }}>
          <Typography
            variant="caption"
            color="text.secondary"
            sx={{ fontSize: "0.8em" }}
          >
            shared by: {file.sender}
          </Typography>
          <Typography
            variant="caption"
            color="error"
            sx={{ fontSize: "0.8em" }}
          >
            Expires on: {new Date(file.expiry).toLocaleDateString()}
          </Typography>
        </Box>
      </Box>
      <IconButton onClick={handleMenuClick}>
        <MoreVertIcon />
      </IconButton>
      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleMenuClose}
      >
        <MenuItem onClick={handleDownload}>Download</MenuItem>
      </Menu>

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
    </Box>
  );
};

export default SharedFileRow;
