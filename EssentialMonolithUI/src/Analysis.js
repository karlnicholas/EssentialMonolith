import React from 'react';
import http from "./http-common";
import Container from "react-bootstrap/Container";
import Button from "react-bootstrap/Button";
import DropdownButton from "react-bootstrap/DropdownButton";
import Table from "react-bootstrap/Table";
import DropdownItem from 'react-bootstrap/esm/DropdownItem';

function handlePopulateClick() {
  http.get('/analysis/populate');
  window.location.reload();
}

export default class Analysis extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      analysisRun: '',
      lastRunDateTime: '',
      count: 0,
      dimensions: [],
      dropdownTitles: [],
      dropdownIds: [],
      olapResult: null
    }
    this.onTargetSelect = this.onTargetSelect.bind(this);
    this.onClick = this.onClick.bind(this);
  }
  componentDidMount() {
    http.get('/analysis').then(response => {
      let titles = this.state.dropdownTitles.slice(); //creates the clone of the state
      titles = new Array(response.data.analysisDimensions.length).fill("");
      let ids = this.state.dropdownIds.slice(); //creates the clone of the state
      ids = new Array(response.data.analysisDimensions.length).fill(null);
      this.setState({
        analysisRun: response.data.analysisRun,
        lastRunDateTime: new Date(response.data.analysisRun.lastRunTime),
        count: response.data.factCount,
        dimensions: response.data.analysisDimensions,
        dropdownTitles: titles, 
        dropdownIds: ids
      });
      // use/access the results 
    }).catch(errors => {
      // react on errors.
    })
  }
  onTargetSelect(name, id, index) {
    let titles = this.state.dropdownTitles.slice(); //creates the clone of the state
    titles[index] = name;
    let ids = this.state.dropdownIds.slice(); //creates the clone of the state
    ids[index] = id;
    this.setState({ dropdownTitles: titles, dropdownIds: ids });
  }
  onClick() {
    var queryString = '';
    var queryMark = '?';
    var i;
    for (i = 0; i < this.state.dropdownTitles.length; ++i) {
      var select = this.state.dropdownIds[i];
      if (select !== null) {
        queryString += queryMark + this.state.dimensions[i].name + '=' + select;
        queryMark = '&';
      }
    }
    http.get('/analysis/billingresult' + queryString).then(response => {
      this.setState({
        olapResult: response.data
      });
      console.log(this.state.olapResult);
    })
  }
  render() {
    const tableHeaders = () => {
      if (this.state.dimensions.length > 0) {
        return (
          this.state.dimensions.map(dimension => <th key={dimension.name}>{dimension.name}</th>)
          );
      }
    }
    const tableBody = () => {
      if (this.state.dimensions.length > 0) {
        return (this.state.dimensions.map((dlist, index) =>
          <td key={dlist.name}>
            <DropdownButton id={dlist.name} title={this.state.dropdownTitles[index]}><DropdownItem value="" onSelect={() => this.onTargetSelect("", null, index)}>&nbsp;</DropdownItem>
              {dlist.dimensions.map(dvalue =>
                <DropdownItem key={dvalue.id} value={dvalue.id} onSelect={() => this.onTargetSelect(dvalue.name, dvalue.id, index)}>
                  {dvalue.name}
                </DropdownItem>)}
            </DropdownButton>
          </td>));
      }
    }
    let { olapResult } = this.state;
    const olapResultDisplay = () => {
      if (olapResult != null) {
        return (
          <div>
            <Table>
              <thead>
              <tr><th>Total</th><th>Mean</th><th>Min</th><th>Max</th><th>Std. Dev.</th></tr>
              </thead>
              <tbody>
              <tr><td>{olapResult.summaryStatistics.sum.toLocaleString()}</td><td>{olapResult.summaryStatistics.mean.toLocaleString()}</td><td>{olapResult.summaryStatistics.min.toLocaleString()}</td><td>{olapResult.summaryStatistics.max.toLocaleString()}</td><td>{olapResult.summaryStatistics.standardDeviation.toLocaleString()}</td></tr>
              </tbody>
            </Table>
            <Table>
              <thead>
                <tr><th>Amount</th><th>Project</th><th>Employee</th><th>Week</th><th>Hours Range</th><th>Rate Range</th></tr>
              </thead>
              <tbody>
              {this.state.olapResult.facts.map(fact => <tr><td>{fact.amount.toLocaleString()}</td><td>{fact.project.name.toLocaleString()}</td><td>{fact.employee.name.toLocaleString()}</td><td>{fact.weekDimension.name.toLocaleString()}</td><td>{fact.hoursRangeDimension.name.toLocaleString()}</td><td>{fact.rateRangeDimension.name.toLocaleString()}</td></tr>)}
              </tbody>
            </Table>
          </div>
        );
      }
    }
    return (
      <Container>
        <Table>
          <thead>
            <tr><th>Populate</th><th>Current Count</th><th>Last Run Time</th><th>Populating</th></tr>
          </thead>
          <tbody>
            <tr><td><Button onClick={handlePopulateClick}>Populate</Button></td><td>{this.state.count}</td><td>{new Intl.DateTimeFormat('en-US', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit' }).format(this.state.lastRunDateTime)}</td><td>{String(this.state.analysisRun.populating)}</td></tr>
          </tbody>
        </Table>
        <Table>
          <thead>
          <tr><th>Query</th>{tableHeaders()}</tr>
          </thead>
          <tbody>
            <tr><td><Button onClick={() => this.onClick()}>Query</Button></td>{tableBody()}</tr>
          </tbody>
        </Table>
        {olapResultDisplay()}
      </Container>
    );
  }
}
