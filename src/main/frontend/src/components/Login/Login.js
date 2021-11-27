import React, { useState } from "react";
import Swal from "sweetalert2";
import { useFormik } from "formik";
import * as Yup from "yup";
import { Button, FloatingLabel, Form } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faEye, faEyeSlash } from "@fortawesome/free-solid-svg-icons";
import { userService } from "../../services";
import styles from "./Login.module.css";

const Login = (props) => {
  const [passwordType, setPasswordType] = useState("password");
  const [isLoading, setLoading] = useState(false);

  const LoginSchema = Yup.object().shape({
    username: Yup.string().required("Enter your username."),
    password: Yup.string().required("Enter your password."),
  });

  const formik = useFormik({
    initialValues: {
      username: "",
      password: "",
    },
    validationSchema: LoginSchema,
    onSubmit: (values) => {
      if (values.username && values.password) {
        setLoading(true);
        userService
          .login(values.username, values.password)
          .then(
            (user) => {
              props.handleLogin();
            },
            (error) => {
              Swal.fire(
                "Error!",
                error.error
                  ? error.error
                  : "Some error occurred. Please try again.",
                "error"
              );
            }
          )
          .finally(() => setLoading(false));
      }
    },
  });

  const toggleShowPassword = () => {
    setPasswordType(passwordType === "password" ? "text" : "password");
  };

  return (
    <form className={styles.form} onSubmit={formik.handleSubmit}>
      <h3>Login</h3>
      <FloatingLabel controlId="floatingUsername" label="Username">
        <Form.Control
          type="text"
          isInvalid={formik.touched.username && !!formik.errors.username}
          disabled={isLoading}
          required
          placeholder="Username"
          name="username"
          value={formik.values.username}
          onChange={formik.handleChange}
        />
        <Form.Control.Feedback type="invalid">
          {formik.errors.username}
        </Form.Control.Feedback>
      </FloatingLabel>
      <FloatingLabel controlId="floatingPassword" label="Password">
        <Form.Control
          type={passwordType}
          isInvalid={formik.touched.password && !!formik.errors.password}
          disabled={isLoading}
          required
          placeholder="Password"
          name="password"
          value={formik.values.password}
          onChange={formik.handleChange}
          className={styles.passwordInput}
        />
        <FontAwesomeIcon
          className={styles.eye}
          icon={passwordType === "text" ? faEye : faEyeSlash}
          onClick={toggleShowPassword}
        />
        <Form.Control.Feedback type="invalid">
          {formik.errors.password}
        </Form.Control.Feedback>
      </FloatingLabel>
      <Button type="primary" disabled={isLoading} onClick={formik.handleSubmit}>
        {isLoading ? "Please wait..." : "Login"}
      </Button>
    </form>
  );
};

export default Login;
