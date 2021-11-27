import React, { useState, useEffect, useRef } from "react";
import { useNavigate, useLocation, Route, Routes } from "react-router-dom";

import AdminNavbar from "../components/Navbars/AdminNavbar";
import Sidebar from "../components/Sidebar/Sidebar";
import routes from "../routes";

import { userService } from "../services";

const App = () => {
  const [login, setLogin] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();
  const mainPanel = useRef(null);

  useEffect(() => {
    let user = JSON.parse(localStorage.getItem("user"));
    if (user?.token) {
      setLogin(true);
    }
  }, []);

  useEffect(() => {
    if (!login && location.pathname !== '/') navigate('/');
  }, [location, login]);

  useEffect(() => {
    document.documentElement.scrollTop = 0;
    document.scrollingElement.scrollTop = 0;
    mainPanel.current.scrollTop = 0;
    if (
      window.innerWidth < 993 &&
      document.documentElement.className.indexOf("nav-open") !== -1
    ) {
      document.documentElement.classList.toggle("nav-open");
      var element = document.getElementById("bodyClick");
      element.parentNode.removeChild(element);
    }
  }, [location]);

  const getRoutes = (routes) => {
    return routes.map((prop, key) => {
      return (
        <Route
          path={prop.layout + prop.path}
          element={<prop.component />}
          key={key}
        />
      );
    });
  };

  const handleLogout = () => {
    userService.logout();
    setLogin(false);
  };

  return (
    <div className="wrapper">
      <Sidebar routes={routes} login={login} />
      <div className="main-panel" ref={mainPanel}>
        <AdminNavbar
          login={login}
          handleLogin={() => setLogin(true)}
          handleLogout={handleLogout}
        />
        <div className="content">
          <Routes>{getRoutes(routes)}</Routes>
        </div>
      </div>
    </div>
  );
};

export default App;
