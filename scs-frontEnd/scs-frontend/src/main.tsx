import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App.tsx";
import "./index.css";
import {
  createBrowserRouter,
  Navigate,
  RouterProvider,
} from "react-router-dom";
import Dashboard from "./components/dashboard/Dashboard.tsx";
import ErrorPage from "./components/error-page/ErrorPage.tsx";
import Landing from "./components/landing/Landing.tsx";
import SignIn from "./components/landing/sign-in/SignIn.tsx";
import SignUp from "./components/landing/sign-up/SignUp.tsx";
import GenerateKeys from "./components/dashboard/generate-keys/GenerateKeys.tsx";
import PublicFiles from "./components/public-files/PublicFiles.tsx";

const routes = [
  {
    path: "/",
    element: <App />,
    errorElement: <ErrorPage />,
    children: [
      {
        path: "/",
        element: <Navigate to="dashboard" replace />,
      },
      {
        path: "landing",
        element: <Landing />,
        children: [
          {
            path: "",
            element: <Navigate to="sign-in" replace />,
          },
          {
            path: "sign-in",
            element: <SignIn />,
          },
          {
            path: "sign-up",
            element: <SignUp />,
          },
        ],
      },
      {
        path: "dashboard",
        element: <Dashboard />,
      },
      {
        path: "public",
        element: <PublicFiles />,
      },
      {
        path: "generate-keys",
        element: <GenerateKeys />,
      },
    ],
  },
];

const router = createBrowserRouter(routes, {
  basename: "/secure-cloud-share/",
});

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>
);
