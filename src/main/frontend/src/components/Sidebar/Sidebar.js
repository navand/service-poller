import React from "react";
import { useLocation, NavLink } from "react-router-dom";
import { Nav } from "react-bootstrap";

const Sidebar = ({ color, image, routes, login }) => {
  const location = useLocation();

  const activeRoute = (routeName) => {
    return location.pathname.indexOf(routeName) > -1 ? "active" : "";
  };

  return (
    <div className="sidebar" data-color="black">
      <div
        className="sidebar-background"
        style={{
          backgroundImage: "url(" + process.env.PUBLIC_URL + "/assets/sidebar.jpg)",
        }}
      />
      <div className="sidebar-wrapper">
        <div className="logo d-flex align-items-center justify-content-start">
          <a
            href="https://www.kry.se/en/"
            className="simple-text logo-mini mx-1"
          >
            <div className="logo-img">
              <img src={process.env.PUBLIC_URL + "/assets/kry-logo-neg.svg"} />
            </div>
          </a>
        </div>
        <Nav>
          {login && routes.map((prop, key) => {
            if (!prop.redirect)
              return (
                <li
                  className={
                    prop.upgrade
                      ? "active active-pro"
                      : activeRoute(prop.layout + prop.path)
                  }
                  key={key}
                >
                  <NavLink
                    to={prop.layout + prop.path}
                    className="nav-link"
                    activeClassName="active"
                  >
                    <i className={`fa ${prop.icon}`}></i>
                    <p>{prop.name}</p>
                  </NavLink>
                </li>
              );
            return null;
          })}
        </Nav>
      </div>
    </div>
  );
}

export default Sidebar;
