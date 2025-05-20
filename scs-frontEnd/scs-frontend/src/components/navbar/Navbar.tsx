import React, { useState, useRef } from "react";
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Menu,
  MenuItem,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import { getUser } from "../../services/auth";
import ArrowDropDownIcon from "@mui/icons-material/ArrowDropDown";

const Navbar: React.FC = () => {
  const navigate = useNavigate();
  const user = getUser();
  const [open, setOpen] = useState(false);
  const anchorRef = useRef<HTMLButtonElement>(null);

  const handleLogout = () => {
    sessionStorage.removeItem("token");
    sessionStorage.removeItem("user");
    navigate("/landing");
  };

  const handleMenuClick = () => {
    setOpen(true);
  };

  const handleMenuClose = () => {
    setOpen(false);
  };

  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" style={{ flexGrow: 1 }}>
          Secure Cloud Share
        </Typography>
        {Object.keys(user).length !== 0 ? (
          <>
            <Button color="inherit" onClick={() => navigate("/dashboard")}>
              Dashboard
            </Button>
            <Button color="inherit" onClick={() => navigate("/public")}>
              Public Files
            </Button>
            {user && (
              <div>
                <Button
                  ref={anchorRef}
                  color="inherit"
                  onClick={handleMenuClick}
                  aria-controls={open ? "user-menu" : undefined}
                  aria-haspopup="true"
                  aria-expanded={open ? "true" : undefined}
                  endIcon={<ArrowDropDownIcon />}
                >
                  {user.userName}
                </Button>
                <Menu
                  id="user-menu"
                  anchorEl={anchorRef.current}
                  open={open}
                  onClose={handleMenuClose}
                  anchorOrigin={{
                    vertical: "bottom",
                    horizontal: "right",
                  }}
                  transformOrigin={{
                    vertical: "top",
                    horizontal: "right",
                  }}
                >
                  <MenuItem onClick={handleLogout}>Logout</MenuItem>
                </Menu>
              </div>
            )}
          </>
        ) : (
          <Button color="inherit" onClick={() => navigate("/landing")}>
            Login
          </Button>
        )}
      </Toolbar>
    </AppBar>
  );
};

export default Navbar;
