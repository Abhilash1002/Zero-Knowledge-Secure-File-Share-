import React, { useEffect, useState } from "react";
import {
  Box,
  Typography,
  CircularProgress,
  Snackbar,
  Alert,
} from "@mui/material";
import {
  retrieveFilesSharedWithMe,
  SharedFileInfo,
} from "../../../services/file-service";
import { getUser } from "../../../services/auth";
import SharedFileRow from "./shared-file-row/SharedFileRow";

const SharedWithMe: React.FC = () => {
  const [files, setFiles] = useState<SharedFileInfo[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [alertMessage, setAlertMessage] = useState<string | null>(null);

  useEffect(() => {
    const fetchSharedFiles = async () => {
      try {
        const email = getUser().email;
        setLoading(true);
        const res = await retrieveFilesSharedWithMe(email);
        if (!res.ok) {
          throw new Error("Something went wrong retrieving the shared files");
        }
        const sharedFiles = (await res.json()) as SharedFileInfo[];
        setFiles(sharedFiles);
      } catch (err) {
        setError("Failed to load shared files");
      } finally {
        setLoading(false);
      }
    };

    fetchSharedFiles();
  }, []);

  return (
    <Box
      sx={{
        height: "100%",
        width: "100%",
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
        Files Shared With Me
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
        files.map((file, index) => <SharedFileRow key={index} file={file} />)
      ) : (
        <Typography variant="body2" color="text.secondary">
          No files shared with you.
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

export default SharedWithMe;
