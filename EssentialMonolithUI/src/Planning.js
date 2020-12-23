import React from "react";
import http from "./http-common";
import Container from "react-bootstrap/Container";
import BootstrapTable from "react-bootstrap-table-next";

export default class Planning extends React.Component {
  state = {
    worklogs: [],
    columns: [
      { dataField: "id", text: "ID" },
      { dataField: "entryDate", text: "Entry Date" },
      { dataField: "hours", text: "Hours" },
      { dataField: "rate", text: "Rate" },
      { dataField: "project.name", text: "Project" },
      { dataField: "project.client.name", text: "Client" },
      { dataField: "employee.name", text: "Employee" },
      { dataField: "employee.department.name", text: "Department" }
    ]
  }
  componentDidMount() {
    http.get('/planning').then(response => {
      this.setState({
        worklogs: response.data
      });
    });
  }
  render() {
    return (
      <Container>
        <BootstrapTable
          keyField='id'
          bootstrap4
          data={this.state.worklogs}
          columns={this.state.columns} />
      </Container>
    );
  }
}
