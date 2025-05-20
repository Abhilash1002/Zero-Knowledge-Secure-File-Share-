import { useEffect } from "react";
import "./App.css";
import Navbar from "./components/navbar/Navbar";
import { Outlet, useNavigate } from "react-router-dom";
import { Box } from "@mui/material";

const App: React.FC = () => {
  const navigate = useNavigate();

  useEffect(() => {
    const jwtToken = sessionStorage.getItem("token");
    if (jwtToken) {
      navigate("/dashboard");
    } else {
      navigate("/landing");
    }
  }, [navigate]);

  return (
    <Box
      sx={{
        bgcolor: "#f0f0f0",
        minHeight: "100vh",
        display: "flex",
        flexDirection: "column",
      }}
    >
      <Navbar />
      <Box
        component="main"
        sx={{
          mt: 1,
          flexGrow: 1,
          overflow: "hidden",
          paddingLeft: 2,
          paddingRight: 2,
        }}
      >
        <Outlet />
      </Box>
    </Box>
  );
};

export default App;
