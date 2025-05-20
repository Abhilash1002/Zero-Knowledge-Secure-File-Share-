import React, { useEffect, useState } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Divider,
  Typography,
  Box,
  Tooltip,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Paper,
  Snackbar,
  Alert,
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import { getUser, User } from "../../../../services/auth";
import {
  retrieveSharedUsers,
  addUserToFileShare,
  removeUserFromFileShare,
  FileInfo,
  ShareFilePayload,
  FileShareResponse,
} from "../../../../services/file-service";
import {
  checkUserExistence,
  getUserPublicKey,
} from "../../../../services/user-service";
import {
  decryptWithPrivateKey,
  encryptWithPublicKey,
  readFileAsText,
} from "../../../../services/crypto";

interface FileShareInfo {
  shareId: number;
  fileId: number;
  userName: string;
  email: string;
  expiry: string; // ISO string format
}

interface FileShareProps {
  open: boolean;
  onClose: () => void;
  file: FileInfo;
}

const FileShare: React.FC<FileShareProps> = ({ open, onClose, file }) => {
  const [sharedUsers, setSharedUsers] = useState<FileShareInfo[]>([]);
  const [newUserInput, setNewUserInput] = useState<string>("");
  const [pendingUser, setPendingUser] = useState<FileShareInfo | null>(null);
  const [expiryDate, setExpiryDate] = useState<string>(
    new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().slice(0, 16)
  ); // Default to one week from now
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [confirmationDialog, setConfirmationDialog] = useState<{
    open: boolean;
    user: FileShareInfo | null;
  }>({ open: false, user: null });
  const [privateKeyDialogOpen, setPrivateKeyDialogOpen] =
    useState<boolean>(false);

  const currentUser = getUser();

  /** Fetch shared users on component mount or when file changes */
  useEffect(() => {
    const fetchSharedUsers = () => {
      setLoading(true);

      retrieveSharedUsers(file.fileId)
        .then((res) => {
          if (!res.ok) {
            throw new Error();
          }
          return res.json() as unknown as FileShareInfo[];
        })
        .then((usersList) => {
          setSharedUsers(usersList);
          setError(null);
        })
        .catch((err) => {
          console.error("Error fetching shared users:", err);
          setError("Failed to load shared users.");
        })
        .finally(() => {
          setLoading(false);
        });
    };

    if (file.fileId && open) {
      fetchSharedUsers();
    }
  }, [file.fileId, open]);

  /** Handle adding new user */
  const handleAddUser = () => {
    setError(null);

    const trimmedInput = newUserInput.trim();

    if (!trimmedInput) {
      setError("Please enter a user email or username.");
      return;
    }

    if (
      trimmedInput === currentUser.email ||
      trimmedInput === currentUser.userName
    ) {
      setError("You cannot add yourself.");
      return;
    }

    if (
      sharedUsers.some(
        (user) => user.email === trimmedInput || user.userName === trimmedInput
      ) ||
      (pendingUser &&
        (pendingUser.email === trimmedInput ||
          pendingUser.userName === trimmedInput))
    ) {
      setError("User is already added.");
      return;
    }

    setLoading(true);

    checkUserExistence(trimmedInput)
      .then((res) => {
        if (!res.ok) {
          throw new Error(
            "Something went wrong while checking if user is valid"
          );
        }
        return res.json() as unknown as User;
      })
      .then((userExists) => {
        if (!userExists) {
          setError("No such user exists.");
          return;
        }

        const newUserInfo: FileShareInfo = {
          shareId: 0, // Will be set by backend
          fileId: file.fileId,
          userName: userExists.userName,
          email: userExists.email,
          expiry: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000) // Default expiry date: 1 week from now
            .toISOString()
            .slice(0, 16),
        };

        setPendingUser(newUserInfo);
        setNewUserInput("");
        setExpiryDate(newUserInfo.expiry);
        setError(null);
      })
      .catch((err) => {
        console.error("Error checking user existence:", err);
        setError("An error occurred while verifying the user.");
      })
      .finally(() => {
        setLoading(false);
      });
  };

  /** Handle saving new user to backend */
  const handleSaveNewUser = () => {
    if (!pendingUser) return;
    // Open the private key upload dialog when "Save" is clicked
    setPrivateKeyDialogOpen(true);
  };

  const handlePrivateKeyUpload = async (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    const privateKeyFile = event.target.files?.[0];
    if (!privateKeyFile || !pendingUser) return;

    try {
      setLoading(true);

      const privateKey = await readFileAsText(privateKeyFile);

      // decrypt the encrypted fileKey and IV with private key
      const decryptedFileKey = await decryptWithPrivateKey(
        privateKey,
        file.key
      );
      const decryptedIV = await decryptWithPrivateKey(privateKey, file.iv);

      // get public Key of recipient
      const recipient = await getUserPublicKey(pendingUser.email).then(
        (res) => {
          if (!res.ok) {
            throw new Error(
              "Something went wrong while fetching user's public key"
            );
          }
          return res.json() as unknown as User;
        }
      );

      // Encrypt the fileKey and IV with public key
      const encryptedFileKey = await encryptWithPublicKey(
        recipient.publicKey!,
        decryptedFileKey
      );
      const encryptedIV = await encryptWithPublicKey(
        recipient.publicKey!,
        decryptedIV
      );

      const payload: ShareFilePayload = {
        fileId: file.fileId,
        senderEmail: currentUser.email,
        receiverEmail: pendingUser.email,
        key: encryptedFileKey,
        iv: encryptedIV,
        expiry: expiryDate,
      };

      addUserToFileShare(payload)
        .then((res) => {
          if (!res.ok) {
            throw new Error("Error sharing file with user");
          }
          return res.json() as unknown as FileShareResponse;
        })
        .then((response) => {
          // Assuming response is of type FileShareResponse
          const newShare: FileShareResponse = response;

          setSharedUsers((prevSharedUsers) => [
            ...prevSharedUsers,
            {
              email: pendingUser.email,
              fileId: file.fileId,
              shareId: newShare.shareId,
              expiry: expiryDate,
              userName: pendingUser.userName,
            },
          ]);
          setPendingUser(null);
          setExpiryDate(
            new Date(Date.now() + 7 * 24 * 60 * 60 * 1000)
              .toISOString()
              .slice(0, 16)
          );
          setSuccessMessage(
            `${pendingUser.userName} has been successfully added to shared users.`
          );
        })
        .catch((error) => {
          console.error("Error adding user to file share:", error);
          setError("Failed to share file with the user.");
        });
    } catch (err) {
      console.error("Error saving new user:", err);
      setError("Failed to share file with the user.");
    } finally {
      setLoading(false);
      setPrivateKeyDialogOpen(false);
    }
  };

  /** Handle removing user */
  const handleRemoveUser = async () => {
    if (!confirmationDialog.user) return;

    try {
      setLoading(true);
      await removeUserFromFileShare(confirmationDialog.user.shareId);

      setSharedUsers(
        sharedUsers.filter(
          (user) => user.shareId !== confirmationDialog.user?.shareId
        )
      );
      setSuccessMessage(
        `${confirmationDialog.user.userName} has been removed from shared users.`
      );
      setConfirmationDialog({ open: false, user: null });
    } catch (err) {
      console.error("Error removing user:", err);
      setError("Failed to remove shared user.");
    } finally {
      setLoading(false);
    }
  };

  /** Handle closing dialogs and resetting states */
  const handleClose = () => {
    setPendingUser(null);
    setNewUserInput("");
    setError(null);
    setSuccessMessage(null);
    setConfirmationDialog({ open: false, user: null });
    onClose();
  };

  return (
    <>
      <Dialog open={open} onClose={handleClose} fullWidth maxWidth="md">
        <DialogTitle>Share File: {file.fileName}</DialogTitle>
        <Divider />
        <DialogContent>
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}
          {successMessage && (
            <Alert severity="success" sx={{ mb: 2 }}>
              {successMessage}
            </Alert>
          )}

          {/* Shared Users Table */}
          <Typography variant="h6" gutterBottom>
            Shared With
          </Typography>
          {loading ? (
            <Typography>Loading shared users...</Typography>
          ) : sharedUsers.length > 0 ? (
            <Paper variant="outlined" sx={{ mb: 3 }}>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>User Name</TableCell>
                    <TableCell>Email</TableCell>
                    <TableCell>Expiry Date</TableCell>
                    <TableCell align="right">Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {sharedUsers.map((user) => (
                    <TableRow key={user.shareId}>
                      <TableCell>{user.userName}</TableCell>
                      <TableCell>{user.email}</TableCell>
                      <TableCell>
                        {new Date(user.expiry).toLocaleString()}
                      </TableCell>
                      <TableCell align="right">
                        <Tooltip title="Remove User">
                          <IconButton
                            color="error"
                            onClick={() =>
                              setConfirmationDialog({ open: true, user })
                            }
                          >
                            <CloseIcon />
                          </IconButton>
                        </Tooltip>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </Paper>
          ) : (
            <Typography>No users have access to this file yet.</Typography>
          )}

          {/* Pending User Form */}
          {pendingUser && (
            <Paper variant="outlined" sx={{ p: 2, mt: 2 }}>
              <Typography variant="subtitle1" gutterBottom>
                Set Expiry Date for {pendingUser.userName}
              </Typography>
              <Box
                sx={{ display: "flex", gap: 2, alignItems: "center", mt: 1 }}
              >
                <TextField
                  label="Expiry Date & Time"
                  type="datetime-local"
                  value={expiryDate}
                  onChange={(e) => setExpiryDate(e.target.value)}
                  InputLabelProps={{
                    shrink: true,
                  }}
                  fullWidth
                />
                <Button
                  variant="contained"
                  color="success"
                  onClick={handleSaveNewUser}
                  disabled={loading}
                >
                  Save
                </Button>
                <Button
                  variant="outlined"
                  color="error"
                  onClick={() => setPendingUser(null)}
                  disabled={loading}
                >
                  Cancel
                </Button>
              </Box>
            </Paper>
          )}

          {/* Add New User Section */}
          <Divider sx={{ my: 2 }} />
          <Typography variant="h6" gutterBottom>
            Add New User
          </Typography>
          <Box sx={{ display: "flex", gap: 2, alignItems: "center", mb: 2 }}>
            <TextField
              label="User Email or Username"
              variant="outlined"
              value={newUserInput}
              onChange={(e) => setNewUserInput(e.target.value)}
              fullWidth
              disabled={loading}
            />
            <Button
              variant="contained"
              color="primary"
              onClick={handleAddUser}
              disabled={loading || !newUserInput.trim()}
            >
              Add User
            </Button>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} disabled={loading}>
            Close
          </Button>
        </DialogActions>
      </Dialog>

      {confirmationDialog.user && (
        <Dialog
          open={confirmationDialog.open}
          onClose={() => setConfirmationDialog({ open: false, user: null })}
        >
          <DialogTitle>Remove Shared User</DialogTitle>
          <DialogContent>
            <Typography>
              Are you sure you want to remove access for{" "}
              <strong>{confirmationDialog.user.userName}</strong>?
            </Typography>
          </DialogContent>
          <DialogActions>
            <Button
              onClick={() => setConfirmationDialog({ open: false, user: null })}
              disabled={loading}
            >
              Cancel
            </Button>
            <Button
              variant="contained"
              color="error"
              onClick={handleRemoveUser}
              disabled={loading}
            >
              Remove
            </Button>
          </DialogActions>
        </Dialog>
      )}

      {/* Private Key Upload Dialog */}
      <Dialog
        open={privateKeyDialogOpen}
        onClose={() => setPrivateKeyDialogOpen(false)}
      >
        <DialogTitle>Upload Private Key</DialogTitle>
        <DialogContent>
          <input
            type="file"
            accept=".pem,.key"
            onChange={handlePrivateKeyUpload}
          />
        </DialogContent>
        <DialogActions>
          <Button
            onClick={() => setPrivateKeyDialogOpen(false)}
            disabled={loading}
          >
            Cancel
          </Button>
        </DialogActions>
      </Dialog>

      {/* Snackbar for Success Messages */}
      <Snackbar
        open={!!successMessage}
        autoHideDuration={6000}
        onClose={() => setSuccessMessage(null)}
      >
        <Alert
          onClose={() => setSuccessMessage(null)}
          severity="success"
          sx={{ width: "100%" }}
        >
          {successMessage}
        </Alert>
      </Snackbar>

      {/* Snackbar for Error Messages */}
      <Snackbar
        open={!!error}
        autoHideDuration={6000}
        onClose={() => setError(null)}
      >
        <Alert
          onClose={() => setError(null)}
          severity="error"
          sx={{ width: "100%" }}
        >
          {error}
        </Alert>
      </Snackbar>
    </>
  );
};

export default FileShare;
