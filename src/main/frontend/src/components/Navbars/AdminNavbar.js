import React, { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import { Navbar, Container, Nav, Modal, Button } from "react-bootstrap";
import Login from "../Login/Login";

const Header = (props) => {
  const { login, handleLogin, handleLogout } = props;
  const location = useLocation();
  const [show, setShow] = useState(false);

  const mobileSidebarToggle = (e) => {
    e.preventDefault();
    document.documentElement.classList.toggle("nav-open");
    var node = document.createElement("div");
    node.id = "bodyClick";
    node.onclick = function () {
      this.parentElement.removeChild(this);
      document.documentElement.classList.toggle("nav-open");
    };
    document.body.appendChild(node);
  };

  return (
    <Navbar bg="light" expand="lg">
      <Container fluid>
        <div className="d-flex justify-content-center align-items-center ml-2 ml-lg-0">
          <Button
            variant="dark"
            className="d-lg-none btn-fill d-flex justify-content-center align-items-center rounded-circle p-2"
            onClick={mobileSidebarToggle}
          >
            <i className="fas fa-ellipsis-v"></i>
          </Button>
        </div>
        <Navbar.Collapse id="basic-navbar-nav" className="justify-content-end">
          <Nav navbar>
            <Nav.Item>
              <Nav.Link
                className="m-0"
                onClick={(e) => {
                  e.preventDefault();
                  if (login) {
                    handleLogout();
                  } else {
                    setShow(true);
                  }
                }}
              >
                <span className="no-icon">{login ? "Log out" : "Login"}</span>
              </Nav.Link>
            </Nav.Item>
          </Nav>
        </Navbar.Collapse>
      </Container>
      <Modal show={show} onHide={() => setShow(false)}>
        <Modal.Body>
          <Login
            handleLogin={() => {
              handleLogin();
              setShow(false);
            }}
          />
        </Modal.Body>
      </Modal>
    </Navbar>
  );
};

export default Header;
