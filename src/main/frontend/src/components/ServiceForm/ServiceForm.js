import React, { useState } from "react";
import Swal from "sweetalert2";
import { useFormik } from "formik";
import * as Yup from "yup";
import { Button, FloatingLabel, Form } from "react-bootstrap";
import { serviceService } from "../../services";
import styles from "./ServiceForm.module.css";

const ServiceForm = (props) => {
  const { state, service, onFinished } = props;
  const [isLoading, setLoading] = useState(false);
  const regexUrl =
    /^(http:\/\/|https:\/\/)(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?$/;

  const ServiceSchema = Yup.object().shape({
    name: Yup.string().required("Enter your name."),
    url: Yup.string()
      .required("Enter your url.")
      .matches(new RegExp(regexUrl.source), "Enter correct url"),
  });

  const formik = useFormik({
    initialValues: {
      name: state === "update" ? service.name : "",
      url: state === "update" ? service.url : "",
    },
    validationSchema: ServiceSchema,
    onSubmit: (values) => {
      if (values.name && values.url) {
        setLoading(true);
        if (state === "update")
          serviceService
            .update({ ...service, ...values })
            .then(
              () => {
                onFinished();
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
        else
          serviceService
            .create(values)
            .then(
              () => {
                onFinished();
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

  return (
    <form className={styles.form} onSubmit={formik.handleSubmit}>
      <h3>{state === "update" ? "Update " : "Create "}Service</h3>
      <FloatingLabel controlId="floatingName" label="Name">
        <Form.Control
          type="text"
          isInvalid={formik.touched.name && !!formik.errors.name}
          disabled={isLoading}
          required
          placeholder="Name"
          name="name"
          value={formik.values.name}
          onChange={formik.handleChange}
        />
        <Form.Control.Feedback type="invalid">
          {formik.errors.name}
        </Form.Control.Feedback>
      </FloatingLabel>
      <FloatingLabel controlId="floatingUrl" label="Url">
        <Form.Control
          type="text"
          isInvalid={formik.touched.url && !!formik.errors.url}
          disabled={isLoading}
          required
          placeholder="Url"
          name="url"
          value={formik.values.url}
          onChange={formik.handleChange}
          className={styles.urlInput}
        />
        <Form.Control.Feedback type="invalid">
          {formik.errors.url}
        </Form.Control.Feedback>
      </FloatingLabel>
      <Button type="primary" disabled={isLoading} onClick={formik.handleSubmit}>
        {isLoading
          ? "Please wait..."
          : state === "update"
          ? "Update"
          : "Create"}
      </Button>
    </form>
  );
};

export default ServiceForm;
