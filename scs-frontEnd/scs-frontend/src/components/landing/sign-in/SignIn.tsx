import React, { ChangeEvent, useState } from "react";
import { login, LoginRequest, LoginResponse } from "../../../services/auth";
import { Link, useNavigate } from "react-router-dom";
import {
  Box,
  Button,
  TextField,
  Typography,
  Alert,
  Container,
} from "@mui/material";
import { SHA512 } from "crypto-js";

const AUTO_LOGOUT_TIME = 120 * 60 * 1000;

const SignIn: React.FC = () => {
  const [email, setEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const handleEmailChange = (event: ChangeEvent<HTMLInputElement>) => {
    setEmail(event.target.value);
  };

  const handlePasswordChange = (event: ChangeEvent<HTMLInputElement>) => {
    setPassword(event.target.value);
  };

  const handleLogin = () => {
    setError(null);

    if (!email || !password) {
      setError("Both email and password are required.");
      return;
    }

    const info: LoginRequest = {
      email: email,
      password: SHA512(password).toString(),
    };

    login(info)
      .then((response) => {
        if (!response?.ok) throw new Error("Email or password invalid");
        return response.json() as unknown as LoginResponse;
      })
      .then((response: LoginResponse) => {
        const { token, ...userInfo } = response;
        sessionStorage.setItem("token", token);
        sessionStorage.setItem("user", JSON.stringify(userInfo));
        navigate("/dashboard");
        setTimeout(() => {
          handleAutoLogout();
        }, AUTO_LOGOUT_TIME);
      })
      .catch((error) => {
        console.log(error);
        setError("Failed to sign in. Please check your email and password.");
      });
  };

  const handleAutoLogout = () => {
    sessionStorage.removeItem("token");
    sessionStorage.removeItem("user");
    navigate("/landing/sign-in");
    alert("You have been automatically logged out due to inactivity.");
  };

  return (
    <Container maxWidth="xs">
      <Box
        sx={{
          mt: 8,
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
        }}
      >
        <Typography component="h1" variant="h5" gutterBottom>
          Sign In
        </Typography>
        <Box
          component="form"
          sx={{
            width: "100%",
            mt: 1,
          }}
          noValidate
        >
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}
          <TextField
            variant="outlined"
            margin="normal"
            required
            fullWidth
            id="email"
            label="Email Address"
            name="email"
            autoComplete="email"
            autoFocus
            value={email}
            onChange={handleEmailChange}
          />
          <TextField
            variant="outlined"
            margin="normal"
            required
            fullWidth
            name="password"
            label="Password"
            type="password"
            id="password"
            autoComplete="current-password"
            value={password}
            onChange={handlePasswordChange}
          />
          <Button
            type="button"
            fullWidth
            variant="contained"
            color="primary"
            sx={{ mt: 3, mb: 2 }}
            onClick={handleLogin}
          >
            Sign In
          </Button>
          <Typography variant="body2" align="center">
            Don't have an account? <Link to="/landing/sign-up">Sign Up</Link>
          </Typography>
        </Box>
      </Box>
    </Container>
  );
};

export default SignIn;
