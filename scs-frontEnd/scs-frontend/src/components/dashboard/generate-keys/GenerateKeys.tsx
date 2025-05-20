import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { User } from "../../../services/auth";
import { Typography, Button, Alert } from "@mui/material";
import {
  exportPrivateKey,
  exportPublicKey,
  generateKeyPair,
} from "../../../services/crypto";
import { updatePublicKey } from "../../../services/user-service";

const GenerateKeys: React.FC = () => {
  const navigate = useNavigate();
  const [privateKey, setPrivateKey] = useState("");
  const [keyGenerated, setKeyGenerated] = useState(false);

  useEffect(() => {
    const user = sessionStorage.getItem("user");
    if (user) {
      const userObj: User = JSON.parse(user) as unknown as User;
      if (userObj.publicKey) {
        // Check if the publicKey exists
        alert("Public key already exists!");
        navigate("/dashboard");
      }
    } else {
      navigate("/landing/sign-in");
    }
  }, [navigate]);

  const savePublicKeyToDB = (publicKey: string) => {
    try {
      const user = JSON.parse(sessionStorage.getItem("user") || "{}") as User;
      const email = user.email;

      if (!email) {
        throw new Error("User email is missing.");
      }

      updatePublicKey({ email, publicKey })
        .then((res) => {
          if (!res.ok) {
            throw new Error("Failed to save the public key");
          }
          return res;
        })
        .then((res) => {
          console.log("Public key saved successfully", res);
          user.publicKey = publicKey;
          sessionStorage.setItem("user", JSON.stringify(user));
        })
        .catch((e) => {
          console.error(e);
          alert("An error occurred while saving the public key.");
        });
    } catch (e) {
      console.error("Error during savePublicKeyToDB execution:", e);
      alert("An error occurred while preparing to save the public key.");
    }
  };

  const handleGenerateKeys = () => {
    generateKeyPair().then((keyPair) => {
      exportPublicKey(keyPair.publicKey).then((pub) => {
        savePublicKeyToDB(pub);
      });
      exportPrivateKey(keyPair.privateKey).then((priv) => setPrivateKey(priv));
      setKeyGenerated(true);
    });
  };

  const downloadFile = (filename: string, content: string, type: string) => {
    const blob = new Blob([content], { type });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
  };

  const handleDownloadPrivateKey = () => {
    if (privateKey) {
      downloadFile("private-key.pem", privateKey, "application/x-pem-file");
    }
  };

  return (
    <div>
      <Typography variant="h4" gutterBottom>
        Generate Your Keys
      </Typography>
      <Typography variant="body1" paragraph>
        Your public key doesn't exist yet. Please generate it now.
      </Typography>
      <Button variant="contained" color="primary" onClick={handleGenerateKeys}>
        Generate Keys
      </Button>
      {keyGenerated && (
        <div>
          <Alert severity="warning" style={{ marginTop: "20px" }}>
            Please save your private key before navigating away. You will not be
            able to retrieve it again.
          </Alert>
          <Button
            variant="contained"
            color="secondary"
            onClick={handleDownloadPrivateKey}
            style={{ marginTop: "20px" }}
          >
            Download Private Key
          </Button>
        </div>
      )}
    </div>
  );
};

export default GenerateKeys;
