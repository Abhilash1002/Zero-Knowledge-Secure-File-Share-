import React, { useEffect, useState } from "react";
import {
  Box,
  Typography,
  CircularProgress,
  Alert,
  IconButton,
  Menu,
  MenuItem,
} from "@mui/material";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import {
  getAllPublicFiles,
  getPublicFile,
  PublicFileContent,
  PublicFileListResponse,
} from "../../services/file-service";
import { base64ToArrayBuffer } from "../../services/crypto";

const PublicFiles: React.FC = () => {
  const [files, setFiles] = useState<PublicFileListResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [selectedFileId, setSelectedFileId] = useState<number | null>(null);

  useEffect(() => {
    const fetchPublicFiles = async () => {
      try {
        const response = await getAllPublicFiles();
        if (!response.ok) {
          throw new Error("Failed to fetch public files");
        }
        const files: PublicFileListResponse[] = await response.json();
        setFiles(files);
      } catch (err) {
        setError("Failed to load public files");
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchPublicFiles();
  }, []);

  const handleMenuClick = (
    event: React.MouseEvent<HTMLElement>,
    fileId: number
  ) => {
    setAnchorEl(event.currentTarget);
    setSelectedFileId(fileId);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
    setSelectedFileId(null);
  };

  const handleDownload = async () => {
    if (selectedFileId !== null) {
      try {
        const response = await getPublicFile(selectedFileId);
        if (!response.ok) {
          throw new Error("Something went wrong while fetching the file");
        }
        const fileData = (await response.json()) as PublicFileContent;

        // // Convert fileData to Blob
        const blob = new Blob([base64ToArrayBuffer(fileData.fileData)], {
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
        console.error("Error downloading file:", error);
      } finally {
        handleMenuClose();
      }
    }
  };

  if (loading) {
    return (
      <Box
        display="flex"
        justifyContent="center"
        alignItems="center"
        height="100%"
      >
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box
        display="flex"
        justifyContent="center"
        alignItems="center"
        height="100%"
      >
        <Alert severity="error">{error}</Alert>
      </Box>
    );
  }

  return (
    <Box
      sx={{
        width: "50%",
        margin: "0 auto",
        padding: 4,
        borderRadius: 2,
        boxShadow: "0px 4px 8px rgba(0, 0, 0, 0.2)",
        backgroundColor: "#f9f9f9",
      }}
    >
      <Typography variant="h5" sx={{ mb: 3, textAlign: "center" }}>
        Public Files
      </Typography>
      {loading ? (
        <Box sx={{ display: "flex", justifyContent: "center", mt: 5 }}>
          <CircularProgress />
        </Box>
      ) : files.length === 0 ? (
        <Typography variant="body1" sx={{ textAlign: "center", color: "gray" }}>
          No public files available
        </Typography>
      ) : (
        files.map((file) => (
          <Box
            key={file.fileId}
            sx={{
              display: "flex",
              alignItems: "center",
              padding: 1,
              borderRadius: 2,
              boxShadow: "0px 2px 4px rgba(0, 0, 0, 0.1)",
              mb: 1,
              backgroundColor: "white",
            }}
          >
            <Box sx={{ flexGrow: 1 }}>
              <Typography variant="body1">{file.fileName}</Typography>
              <Typography variant="body2" color="text.secondary">
                {file.fileOwner}
              </Typography>
            </Box>
            <IconButton
              onClick={(event) => handleMenuClick(event, file.fileId)}
            >
              <MoreVertIcon />
            </IconButton>
          </Box>
        ))
      )}
      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleMenuClose}
      >
        <MenuItem onClick={handleDownload}>Download</MenuItem>
      </Menu>
    </Box>
  );
};

export default PublicFiles;
