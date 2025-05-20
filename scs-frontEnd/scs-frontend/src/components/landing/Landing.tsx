import React from "react";
import { Outlet } from "react-router-dom";
import { Grid, Box, Typography, Container } from "@mui/material";

const Landing: React.FC = () => {
  return (
    <Grid container sx={{ height: "calc(100vh - 80px)" }} spacing={2}>
      <Grid item xs={12} md={6}>
        <Box
          display="flex"
          flexDirection="column"
          justifyContent="center"
          alignItems="center"
          height="100%"
          bgcolor="primary.main"
          color="white"
          sx={{
            borderRadius: "16px", // Rounded corners
            boxShadow: "0px 4px 12px rgba(0, 0, 0, 0.1)", // Depth
          }}
        >
          <Container>
            <Typography variant="h3" gutterBottom>
              Welcome to Secure Cloud Share
            </Typography>
            <Typography variant="h6">
              Share your files securely and easily.
            </Typography>
          </Container>
        </Box>
      </Grid>
      <Grid item xs={12} md={6}>
        <Box
          display="flex"
          flexDirection="column"
          justifyContent="center"
          alignItems="center"
          height="100%"
          sx={{
            backgroundColor: "#e0f7fa", // Neutral background
            borderRadius: "16px", // Rounded corners
            boxShadow: "0px 4px 12px rgba(0, 0, 0, 0.1)", // Depth
          }}
        >
          <Outlet />
        </Box>
      </Grid>
    </Grid>
  );
};

export default Landing;
