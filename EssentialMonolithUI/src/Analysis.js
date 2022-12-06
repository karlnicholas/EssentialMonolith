import React, { useState, useEffect } from 'react';
import http from "./http-common";
import Container from "react-bootstrap/Container";
import Button from "react-bootstrap/Button";
import DropdownButton from "react-bootstrap/DropdownButton";
import Table from "react-bootstrap/Table";
import DropdownItem from 'react-bootstrap/esm/DropdownItem';
import Card from "react-bootstrap/Card";
import CardGroup from "react-bootstrap/CardGroup";
import ListGroup from "react-bootstrap/ListGroup";

const AnalysisPage = () => {
    const [analysisRun, setAnalysisRun] = useState(null);
    const [dimensions, setDimensions] = useState([]);
    const [olapResult, setOlapResult] = useState(null);
    const [whereSelects, setWhereSelects] = useState([]);
    const [factCount, setFactCount] = useState(0);

    useEffect(() => {
      http.get('/analysis').then(response => {
        setDimensions(response.data.analysisDimensions);
        setFactCount(response.data.factCount);
        // use/access the results 
      }).catch(errors => {
        // react on errors.
      });
    }, []);

  //   componentDidMount() {
  //   http.get('/analysis').then(response => {
  //     let titles = this.state.dropdownTitles.slice(); //creates the clone of the state
  //     titles = new Array(response.data.analysisDimensions.length).fill("");
  //     let ids = this.state.dropdownIds.slice(); //creates the clone of the state
  //     ids = new Array(response.data.analysisDimensions.length).fill(null);
  //     this.setState({
  //       analysisRun: response.data.analysisRun,
  //       lastRunDateTime: new Date(response.data.analysisRun.lastRunTime),
  //       count: response.data.factCount,
  //       dimensions: response.data.analysisDimensions,
  //       dropdownTitles: titles, 
  //       dropdownIds: ids, 
  //     });
  //     // use/access the results 
  //   }).catch(errors => {
  //     // react on errors.
  //   })
  // }
  return (
    <Container>
      <Table>
        <thead>
          <tr><th>Populate</th><th>Fact Count</th><th>Last Run Time</th><th>Populating</th></tr>
        </thead>
        <tbody>
          {showAnalysisRun(analysisRun, factCount, setAnalysisRun, setDimensions, setFactCount)}
        </tbody>
      </Table>
      <CardGroup>
        <Card>{card(dimensions)}</Card>
        <Card><ListGroup>{whereGroup(whereSelects)}</ListGroup></Card>
      </CardGroup>
      <Table>
        <thead>
        <tr><th>Query</th>{tableHeaders(dimensions)}</tr>
        </thead>
        <tbody>
          <tr><td><Button onClick={() => onClick(dimensions, setOlapResult)}>Query</Button></td>{tableBody(dimensions)}</tr>
        </tbody>
      </Table>
      {olapResultDisplay(olapResult)}
    </Container>
  );
}
  const onTargetSelect = (name, id, index) => {
    let titles = this.state.dropdownTitles.slice(); //creates the clone of the state
    titles[index] = name;
    let ids = this.state.dropdownIds.slice(); //creates the clone of the state
    ids[index] = id;
    this.setState({ dropdownTitles: titles, dropdownIds: ids });
  };
  const onWhereSelect = (name, id, index) => {
    let d = this.state.dimensions[index];
    let dname = d.name;
    let whereSelects = this.state.whereSelects.slice(); //creates the clone of the state
    whereSelects.push({'display': dname + ':' + name + ":" + id, 'property':dname, 'id': id});
    // this.setState({ whereSelects: whereSelects });
    // var whereList = [];
    // var i;
    // for (i = 0; i < this.state.whereSelects.length; ++i) {
    //   whereList.push({'property': this.state.whereSelects[i].property, 'id': this.state.whereSelects[i].id});
    // }
    // (async()=>{
    //   http.post('/analysis/olap', whereList).then(response => {
    //     this.setState({
    //       olapResult: response.data
    //     });
    //   })
    //  })();
  };

  function handlePopulateClick(setAnalysisRun) {
    http.get('/analysis/populate').then(response => {
      setAnalysisRun(response.data);
    });
  }
  function handleRefreshClick(setDimensions, setAnalysisRun, setFactCount) {
    http.get('/analysis').then(response => {
      setDimensions(response.data.analysisDimensions);
      setAnalysisRun(response.data.analysisRun);
      setFactCount(response.data.factCount);
    });
  }
    const onClick = (dimensions, dropdownTitles, dropdownIds, setOlapResult) => {
    var queryString = '';
    var queryMark = '?';
    var i;
    for (i = 0; i < dropdownTitles.length; ++i) {
      var select = dropdownIds[i];
      if (select !== null) {
        queryString += queryMark + dimensions[i].name + '=' + select;
        queryMark = '&';
      }
    }
    (async()=>{
      http.get('/analysis/billingresult' + queryString).then(response => {
        setOlapResult(response.data);
      })
     })();
  };
    const tableHeaders = (dimensions) => {
      if (dimensions.length > 0) {
        return (
          dimensions.map(dimension => <th key={dimension.name}>{dimension.name}</th>)
          );
      }
    };
    const card = (dimensions) => {
      if (dimensions.length > 0) {
        return (dimensions.map((dlist, index) =>
              <DropdownButton key={dlist.name} id={dlist.name} title={dlist.name}>
                {dlist.dimensions.map(dvalue =>
                  <DropdownItem key={dvalue.id} value={dvalue.id} onClick={() => this.onWhereSelect(dvalue.name, dvalue.id, index)}>
                    {dvalue.name}
                  </DropdownItem>)}
              </DropdownButton>
            ));
        
      }
    }
    const whereGroup = (whereSelects) => {
      if (whereSelects.length > 0) {
        return (whereSelects.map((w, index) =>
              <ListGroup.Item key={index}>{w.display}</ListGroup.Item>
            ));
        
      }
    }
    const tableBody = (dimensions) => {
      if (dimensions.length > 0) {
        return (dimensions.map((dlist, index) =>
          <td key={dlist.name}>
            <DropdownButton id={dlist.name} title={dimensions[index].title !== undefined ? dimensions[index].title: ''}><DropdownItem value="" onClick={() => this.onTargetSelect("", null, index)}>&nbsp;</DropdownItem>
              {dlist.dimensions.map(dvalue =>
                <DropdownItem key={dvalue.id} value={dvalue.id} onClick={() => this.onTargetSelect(dvalue.name, dvalue.id, index)}>
                  {dvalue.name}
                </DropdownItem>)}
            </DropdownButton>
          </td>));
      }
    }
    const showAnalysisRun = (analysisRun, factCount, setAnalysisRun, setDimensions, setFactCount) => {
      if ( analysisRun != null ) {
        return (
          <tr><td>{analysisRun.populating ? <Button onClick={() => handleRefreshClick(setDimensions, setAnalysisRun, setFactCount)}>Refresh</Button> : <Button onClick={() => handlePopulateClick(setAnalysisRun)}>Populate</Button>}</td><td>{factCount}</td><td>{new Intl.DateTimeFormat('en-US', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit' }).format(analysisRun.lastRunDateTime)}</td><td>{String(analysisRun.populating)}</td></tr>
        );
      } else {
        return (
          <tr><td><Button onClick={() => handlePopulateClick(setAnalysisRun)}>Populate</Button></td><td>No Count</td><td>Not Run</td><td>FALSE</td></tr>
        );
      }
    }
    const olapResultDisplay = (olapResult) => {
      if (olapResult != null) {
        return (
          <div>
            <Table>
              <thead>
              <tr><th>Total</th><th>Mean</th><th>Min</th><th>Max</th><th>Std. Dev.</th></tr>
              </thead>
              <tbody>
              Fucked this up
              </tbody>
            </Table>
            <Table>
              <thead>
                <tr><th>Amount</th><th>Project</th><th>Employee</th><th>Week</th><th>Hours Range</th><th>Rate Range</th></tr>
              </thead>
              <tbody>
              {olapResult.facts.map((fact, index) => <tr key={index}><td>{fact.amount.toLocaleString()}</td><td>{fact.project.name.toLocaleString()}</td><td>{fact.employee.name.toLocaleString()}</td><td>{fact.weekDimension.name.toLocaleString()}</td><td>{fact.hoursRangeDimension.name.toLocaleString()}</td><td>{fact.rateRangeDimension.name.toLocaleString()}</td></tr>)}
              </tbody>
            </Table>
          </div>
        );
      }
    }


export default function Analysis() {
  return <AnalysisPage/>
};