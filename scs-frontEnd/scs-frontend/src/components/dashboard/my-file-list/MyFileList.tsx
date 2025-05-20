import React, { useEffect, useState } from "react";
import {
  Box,
  Typography,
  CircularProgress,
  Snackbar,
  Alert,
} from "@mui/material";
import FileItem from "./file-item/FileItem";
import { FileInfo, retrieveMyFileList } from "../../../services/file-service";
import { getUser } from "../../../services/auth";

interface MyFileListProps {
  refresh: boolean;
}

const MyFileList: React.FC<MyFileListProps> = ({ refresh }) => {
  const [files, setFiles] = useState<FileInfo[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [alertMessage, setAlertMessage] = useState<string | null>(null);

  const deleteFile = (fileId: number) => {
    setFiles((prevFiles) => prevFiles.filter((file) => file.fileId !== fileId));
    setAlertMessage("File deleted successfully " + fileId);
  };

  useEffect(() => {
    const fetchMyFiles = async () => {
      try {
        const email = getUser().email;
        setLoading(true);
        const res = await retrieveMyFileList(email);
        if (!res.ok) {
          throw new Error("Something went wrong retrieving your file list");
        }
        const files = (await res.json()) as FileInfo[];
        setFiles(files);
      } catch (err) {
        setError("Failed to load files");
      } finally {
        setLoading(false);
      }
    };

    fetchMyFiles();
  }, [refresh]);

  return (
    <Box
      sx={{
        height: "100%",
        bgcolor: "background.paper",
        borderRadius: "16px",
        boxShadow: "0px 4px 8px rgba(0, 0, 0, 0.1)",
        display: "flex",
        flexDirection: "column",
        overflowY: "auto",
        padding: 2,
      }}
    >
      <Typography variant="h6" gutterBottom>
        My Files
      </Typography>

      {loading ? (
        <Box
          sx={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            height: "100%",
            width: "100%",
          }}
        >
          <CircularProgress />
        </Box>
      ) : error ? (
        <Typography variant="body2" color="error">
          {error}
        </Typography>
      ) : files.length > 0 ? (
        files.map((file, index) => (
          <FileItem key={index} file={file} onDelete={deleteFile} />
        ))
      ) : (
        <Typography variant="body2" color="text.secondary">
          No files available.
        </Typography>
      )}

      <Snackbar
        open={Boolean(alertMessage)}
        autoHideDuration={6000}
        onClose={() => setAlertMessage(null)}
      >
        <Alert
          onClose={() => setAlertMessage(null)}
          severity={error ? "error" : "success"}
          sx={{ width: "100%" }}
        >
          {alertMessage}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default MyFileList;
