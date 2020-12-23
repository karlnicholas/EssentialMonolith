import React from "react";
import axios from 'axios';
import Container from "react-bootstrap/Container";
import BootstrapTable from "react-bootstrap-table-next";

export default class Home extends React.Component {
  state = {
    employees: [],
    columns: [
      { dataField: "id", text: "Product ID" },
      { dataField: "name", text: "Product Name" },
      { dataField: "department.name", text: "Department" }
    ]
  }
  componentDidMount() {
    axios.get('http://localhost:8080/hr').then(response => {
      this.setState({
        employees: response.data
      });
    });
  }
  render() {
    return (
      <Container>
        <BootstrapTable
          bootstrap4
          keyField='id'
          data={this.state.employees}
          columns={this.state.columns} />
      </Container>
    );
  }
}
