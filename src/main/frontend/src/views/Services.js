import React, { useState, useEffect, useRef } from "react";
import BootstrapTable from "react-bootstrap-table-next";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faEdit,
  faPlusSquare,
  faSpinner,
  faTrashAlt,
} from "@fortawesome/free-solid-svg-icons";
import { Button, Container, Modal } from "react-bootstrap";
import { confirmAlert } from 'react-confirm-alert';
import Swal from "sweetalert2";
import _ from "lodash";

import ServiceForm from "../components/ServiceForm/ServiceForm";
import { serviceService } from "../services";
import styles from "./Services.module.css";
import 'react-confirm-alert/src/react-confirm-alert.css';

const Services = () => {
  const [services, setServices] = useState([]);
  const [show, setShow] = useState(false);
  const [loading, setLoading] = useState(false);
  const stateRef = useRef();
  const tableRef = useRef();
  const selectedServiceRef = useRef();
  const columns = [
    {
      dataField: "id",
      text: "ID",
    },
    {
      dataField: "name",
      text: "Service Name",
    },
    {
      dataField: "url",
      text: "Service URL",
    },
    {
      dataField: "status",
      text: "Service Status",
    },
    {
      dataField: "created_at",
      text: "Service Created Date/Time",
    },
  ];

  useEffect(() => {
    serviceService.findAll().then((response) => {
      setServices(response.services);
    }).catch(() => { });

    const getAllServicesTimer = setInterval(() => {
      serviceService.findAll().then((response) => {
        setServices(response.services);
      }).catch(() => { });
    }, 2000);

    return () => {
      if (getAllServicesTimer) clearInterval(getAllServicesTimer);
    };
  }, []);

  const createService = () => {
    stateRef.current = "create";
    setShow(true);
  };

  const updateService = () => {
    if (!selectedServiceRef.current)
      Swal.fire("Note!", "Please select a service for edit.", "warning");
    else {
      stateRef.current = "update";
      setShow(true);
    }
  };

  const deleteService = () => {
    if (!selectedServiceRef.current)
      Swal.fire("Note!", "Please select a service for delete.", "warning");
    else {
      confirmAlert({
        title: '',
        message: 'Are you sure to delete the service?',
        buttons: [
          {
            label: 'No',
            onClick: () => { },
          },
          {
            label: 'Yes',
            onClick: () => handleDeleteService(),
          },
        ],
      });
    }
  };

  const handleDeleteService = () => {
    setLoading(true);
    serviceService
      .delete(selectedServiceRef.current.id)
      .then(
        () => {},
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

  return (
    <Container fluid>
      <div className={styles.controls}>
        <Button variant="success" onClick={createService}>
          <FontAwesomeIcon icon={faPlusSquare} />
        </Button>
        <Button variant="primary" onClick={updateService}>
          <FontAwesomeIcon icon={faEdit} />
        </Button>
        <Button variant="danger" onClick={deleteService} disabled={loading}>
          <FontAwesomeIcon icon={loading ? faSpinner : faTrashAlt} />
        </Button>
      </div>
      <BootstrapTable
        ref={tableRef}
        bootstrap4
        keyField="id"
        data={services}
        columns={columns}
        selectRow={{
          mode: "radio",
          clickToSelect: true,
          onSelect: (row, isSelect, rowIndex, e) => {
            selectedServiceRef.current = row;
          },
        }}
      />
      <Modal show={show} onHide={() => setShow(false)}>
        <Modal.Body>
          <ServiceForm
            state={stateRef.current}
            service={selectedServiceRef.current}
            onFinished={() => {
              setShow(false);
            }}
          />
        </Modal.Body>
      </Modal>
    </Container>
  );
};

export default Services;
