import React, { useState, useCallback } from "react";
import { useDropzone } from "react-dropzone";
import {
  Box,
  Typography,
  List,
  ListItem,
  ListItemText,
  IconButton,
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import FileUploadHandler from "./FileUploadHandler";

interface FileUploadAreaProps {
  refreshOnUpload: () => void;
}

const FileUploadArea: React.FC<FileUploadAreaProps> = ({ refreshOnUpload }) => {
  const [uploadedFiles, setUploadedFiles] = useState<File[]>([]);

  const onDrop = useCallback((acceptedFiles: File[]) => {
    setUploadedFiles((prevFiles) => [...prevFiles, ...acceptedFiles]);
  }, []);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({ onDrop });

  const deleteFile = (index: number) => {
    setUploadedFiles((prevFiles) => prevFiles.filter((_, i) => i !== index));
  };

  const handleUploadComplete = () => {
    setUploadedFiles([]);
    refreshOnUpload();
  };

  return (
    <Box sx={{ width: "100%", maxWidth: 500, margin: "auto" }}>
      <Box
        {...getRootProps()}
        sx={{
          border: "2px dashed #cccccc",
          borderRadius: 2,
          padding: 3,
          textAlign: "center",
          cursor: "pointer",
          backgroundColor: isDragActive ? "#f0f0f0" : "white",
        }}
      >
        <input {...getInputProps()} />
        <Typography variant="body1">
          {isDragActive
            ? "Drop the files here ..."
            : "Drag 'n' drop some files here, or click to select files"}
        </Typography>
      </Box>

      {uploadedFiles.length > 0 && (
        <>
          <List
            sx={{
              mt: 2,
              maxHeight: "50vh",
              overflowY: "auto",
              border: "1px solid #cccccc",
              borderRadius: 1,
            }}
          >
            {uploadedFiles.map((file, index) => (
              <ListItem
                key={index}
                secondaryAction={
                  <IconButton
                    edge="end"
                    aria-label="delete"
                    onClick={() => deleteFile(index)}
                  >
                    <DeleteIcon />
                  </IconButton>
                }
              >
                <ListItemText
                  primary={file.name}
                  secondary={`${file.size} bytes`}
                />
              </ListItem>
            ))}
          </List>
          <br />
          <FileUploadHandler
            files={uploadedFiles}
            onUploadComplete={handleUploadComplete}
          />
        </>
      )}
    </Box>
  );
};

export default FileUploadArea;
