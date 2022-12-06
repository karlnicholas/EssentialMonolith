import React from "react";
import http from "./http-common";

export default class Planning extends React.Component {
  state = {
    worklogs: []
  }
  componentDidMount() {
    http.get('/planning').then(response => {
      this.setState({
        worklogs: response.data
      });
    });
  }
  render() {
    const tableBody = () => {
      return (
        this.state.worklogs.map(r => <tr key={r.id}>
          <td>{r.id}</td>
          <td>{r.entryDate}</td>
          <td>{r.hours}</td>
          <td>{r.rate}</td>
          <td>{r.project.name}</td>
          <td>{r.project.client.name}</td>
          <td>{r.employee.name}</td>
          <td>{r.employee.department.name}</td>
          </tr>)
        );
    }
    return (
      <div>
        <table className='table'>
          <thead>
          <tr><th>ID</th><th>Entry Date</th><th>Hours</th><th>Rate</th><th>Project</th><th>Client</th><th>Employee</th><th>Department</th></tr>
          </thead>
          <tbody>{tableBody()}</tbody>
        </table>
      </div>
    );
  }
}
