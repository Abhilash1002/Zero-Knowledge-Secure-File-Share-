import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Grid, Box, Typography } from "@mui/material";
import FileUploadArea from "./FileUpload/FileUploadArea";
import MyFileList from "./my-file-list/MyFileList";
import { getUser } from "../../services/auth";
import SharedWithMe from "./shared-with-me/SharedWithMe";

const Dashboard: React.FC = () => {
  const navigate = useNavigate();
  const [refreshFiles, setRefreshFiles] = useState<boolean>(false);

  useEffect(() => {
    const jwtToken = sessionStorage.getItem("token");
    if (!jwtToken) {
      navigate("/landing");
    }

    const user = getUser();

    if (!user.publicKey) {
      navigate("/generate-keys");
    }
  }, [navigate]);

  const refreshOnUpload = () => {
    setRefreshFiles((prev) => !prev);
  };

  return (
    <Grid container spacing={2} sx={{ height: "calc(100vh - 80px)" }}>
      <Grid item xs={12} md={6}>
        <Grid container spacing={2} sx={{ height: "100%" }}>
          <Grid item xs={12} sx={{ height: "50%", mb: 2 }}>
            <Box
              sx={{
                display: "flex",
                flexDirection: "column",
                height: "100%",
                backgroundColor: "white",
                borderRadius: "16px",
                boxShadow: "0px 4px 12px rgba(0, 0, 0, 0.1)",
              }}
            >
              <MyFileList refresh={refreshFiles} />
            </Box>
          </Grid>
          <Grid item xs={12} sx={{ height: "50%" }}>
            <Box
              display="flex"
              justifyContent="center"
              alignItems="center"
              height="100%"
              sx={{
                backgroundColor: "white",
                borderRadius: "16px",
                boxShadow: "0px 4px 12px rgba(0, 0, 0, 0.1)",
              }}
            >
              <SharedWithMe />
            </Box>
          </Grid>
        </Grid>
      </Grid>
      <Grid item xs={12} md={6}>
        <Box
          display="flex"
          justifyContent="center"
          alignItems="center"
          height="100%"
          padding="8px 0px 8px 0px"
          sx={{
            backgroundColor: "white",
            borderRadius: "16px",
            boxShadow: "0px 4px 12px rgba(0, 0, 0, 0.1)",
          }}
        >
          <Typography variant="h6">
            <FileUploadArea refreshOnUpload={refreshOnUpload} />
          </Typography>
        </Box>
      </Grid>
    </Grid>
  );
};

export default Dashboard;
