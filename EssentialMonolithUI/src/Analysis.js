import React from 'react';
import axios from 'axios';
import Container from "react-bootstrap/Container";
import Card from "react-bootstrap/Card";
import Button from "react-bootstrap/Button";
import DropdownButton from "react-bootstrap/DropdownButton";
import Table from "react-bootstrap/Table";
import DropdownItem from 'react-bootstrap/esm/DropdownItem';

function handlePopulateClick() {
  axios.get('http://localhost:8080/analysis/populate');
  window.location.reload();
}

export default class Analysis extends React.Component {
  constructor(props) {
    super(props);
    this.onTargetSelect = this.onTargetSelect.bind(this);
    this.onClick = this.onClick.bind(this);
    this.state = {
      lastRun: '',
      lastRunDateTime: '',
      count: 0,
      dimensions: [], 
      dropdownTitles: [], 
      queryStats: null
    }
  }
  tableHeaders = null;
  tableBody = null;
  tableBody2 = null;
  componentDidMount() {
    const requestOne = axios.get('http://localhost:8080/analysis');
    const requestTwo = axios.get('http://localhost:8080/analysis/count');
    const requestThree = axios.get('http://localhost:8080/analysis/billingdimensions');
    axios.all([requestOne, requestTwo, requestThree]).then(axios.spread((...responses) => {
      let a = this.state.dropdownTitles.slice(); //creates the clone of the state
      a = new Array(responses[2].data.length).fill("");
      this.setState({
        lastRun: responses[0].data,
        lastRunDateTime: new Date(responses[0].data.lastRunTime),
        count: responses[1].data,
        dimensions: responses[2].data, 
        dropdownTitles: a
      });
      // use/access the results 
    })).catch(errors => {
      // react on errors.
    })
  }
  onTargetSelect(target, index) {
    console.log(target);
    let a = this.state.dropdownTitles.slice(); //creates the clone of the state
    a[index] = target;
    this.setState({dropdownTitles: a});
  }
  onClick() {
    console.log("CLICK");
    var queryString = '';
    var queryMark = '?';
    var i;
    for ( i = 0; i < this.state.dropdownTitles.length; ++i ) {
      var select = this.state.dropdownTitles[i];
      if ( select !==  "" ) {
        queryString += queryMark + this.state.dimensions[i].name + '=' + select;
        queryMark = '&';
      }
    }
    axios.get('http://localhost:8080/analysis/billingresult'+queryString).then(response => {
      this.setState({
        queryStats: response.data
      });
      console.log(this.state.queryStats);
    })
  }
  render() {
    if (this.state.dimensions.length > 0) {
      console.log(this.state.dropdownTitles);
      this.tableHeaders = this.state.dimensions.map(dimension => <th key={dimension.name}>{dimension.name}</th>);
      this.tableBody = this.state.dimensions.map(dlist => <td key={dlist.name}><select id={dlist.name}><option value=""></option>
      {dlist.dimensions.map(dvalue => <option key={dvalue.id} value={dvalue.id}>{dvalue.name}</option>)}
      </select></td>);
      this.tableBody2 = this.state.dimensions.map((dlist, index) => 
        <td key={dlist.name}>
          <DropdownButton id={dlist.name} title={this.state.dropdownTitles[index]}><DropdownItem value="" onSelect={() => this.onTargetSelect("", index)}>&nbsp;</DropdownItem>
          {dlist.dimensions.map(dvalue => 
            <DropdownItem key={dvalue.id} value={dvalue.id} onSelect={() => this.onTargetSelect(dvalue.name, index)}>
              {dvalue.name}
            </DropdownItem>)}
          </DropdownButton>
        </td>);
    } else {
      console.log("Skip");
    }
    let {queryStats} = this.state;
    const queryResultDisplay = () => {
      if ( queryStats != null ) {
        return (
          <Card>
          <Card.Body>
            <Card.Title>Amount</Card.Title><Card.Text>{queryStats.n * queryStats.mean}</Card.Text>
            <Card.Title>N</Card.Title><Card.Text>{queryStats.n}</Card.Text>
            <Card.Title>Mean</Card.Title><Card.Text>{queryStats.mean}</Card.Text>
            <Card.Title>Min</Card.Title><Card.Text>{queryStats.min}</Card.Text>
            <Card.Title>Max</Card.Title><Card.Text>{queryStats.max}</Card.Text>
          </Card.Body>
          </Card>
        );
      }
    }
    return (
      <Container>
        <Card>
          <Card.Body>
            <Card.Title>Current Count</Card.Title><Card.Text>{this.state.count}</Card.Text>
            <Card.Title>Last Run Time</Card.Title><Card.Text>{new Intl.DateTimeFormat('en-US', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit' }).format(this.state.lastRunDateTime)}</Card.Text>
            <Card.Title>Populating</Card.Title><Card.Text>{String(this.state.lastRun.populating)}</Card.Text>
            <Button onClick={handlePopulateClick}>Populate</Button>
          </Card.Body>
        </Card>
        <Table>
          <thead>
            <tr>{this.tableHeaders}</tr>
          </thead>
          <tbody>
            <tr>{this.tableBody2}</tr>
          </tbody>
        </Table>
        <Button onClick={()=>this.onClick()}>Query</Button>
        {queryResultDisplay()}
      </Container>
    );
  }
}
