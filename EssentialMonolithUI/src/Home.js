import React from "react";
import http from "./http-common";

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
    http.get('/hr').then(response => {
      this.setState({
        employees: response.data
      });
    });
  }
  render() {
    const tableBody = () => {
      return (
        this.state.employees.map(e => <tr key={e.id}>
          <td>{e.id}</td>
          <td>{e.name}</td>
          <td>{e.department.name}</td>
          </tr>)
        );
    }
    return (
      <div>
        <table className="table">
          <thead>
          <tr><th>Product ID</th><th>Product Name</th><th>Department</th></tr>
          </thead>
          <tbody>{tableBody()}</tbody>
        </table>
      </div>
    );
  }
}
