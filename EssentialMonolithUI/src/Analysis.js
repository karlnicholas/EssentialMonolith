import React, { useState, useEffect } from 'react';
import http from "./http-common";

const AnalysisPage = () => {
  const [analysisRun, setAnalysisRun] = useState(null);
  const [dimensions, setDimensions] = useState([]);
  const [olapResult, setOlapResult] = useState(null);
  const [whereSelects, setWhereSelects] = useState([]);
  const [factCount, setFactCount] = useState(0);

  useEffect(() => {
    http.get('/analysis').then(response => {
      setDimensions(response.data.analysisDimensions);
      setAnalysisRun(response.data.analysisRun);
      setFactCount(response.data.factCount);
    });
  }, []);

  return (
    <div>
      <table className="table">
        <thead>
          <tr><th>Populate</th><th>Fact Count</th><th>Last Run Time</th><th>Populating</th></tr>
        </thead>
        <tbody>
          {showAnalysisRun(analysisRun, factCount, setAnalysisRun, setDimensions, setFactCount)}
        </tbody>
      </table>
      <div>
        {card(dimensions)}
      </div>
      <table className="table">
        <thead>
          <tr><th>Query</th>{tableHeaders(dimensions)}</tr>
        </thead>
        <tbody>
          <tr><td><button className="btn btn-primary" onClick={() => onQueryClick(dimensions, setOlapResult)}>Query</button></td>{tableBody(dimensions)}</tr>
        </tbody>
      </table>
      {olapResultDisplay(olapResult)}
    </div>
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
  whereSelects.push({ 'display': dname + ':' + name + ":" + id, 'property': dname, 'id': id });
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
function onQueryClick(dimensions, dropdownTitles, dropdownIds, setOlapResult) {
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
  (async () => {
    http.get('/analysis/billingresult' + queryString).then(response => {
      setOlapResult(response.data);
    })
  })();
};
function tableHeaders(dimensions) {
  if (dimensions.length > 0) {
    return (
      dimensions.map(dimension => <th key={dimension.name}>{dimension.name}</th>)
    );
  }
};
function card(dimensions) {
  if (dimensions.length > 0) {
    return (dimensions.map((dlist, index) =>
      <div key={index} className="dropdown">
        <button className="btn btn-primary dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false" key={dlist.name} id={dlist.name}>{dlist.name}</button >
        <ul className="dropdown-menu">
          {dlist.dimensions.map((dvalue, index) =>
            <li key={index}><a className="dropdown-item" href="#" onClick={() => onWhereSelect(dvalue.name, dvalue.id, index)}>{dvalue.name}</a></li>
          )}
        </ul>
      </div>
    ));
  }
}
function whereGroup(whereSelects) {
  if (whereSelects.length > 0) {
    return (whereSelects.map((w, index) =>
      <il key={index}>{w.display}</il>
    ));

  }
}
function tableBody(dimensions) {
  if (dimensions.length > 0) {
    return (dimensions.map((dlist, index) =>
      <td key={dlist.name}>
        <div key={index} className="dropdown">
          <button className='btn btn-primary dropdown-toggle' type="button" data-bs-toggle="dropdown" aria-expanded="false" id={dlist.name}>{dimensions[index].title !== undefined ? dimensions[index].title : ''}</button >
          <ul className="dropdown-menu">
            <li key={index}><button className='dropdown-item' type="button" onClick={() => onTargetSelect("", null, index)}>&nbsp;</button></li>
            {dlist.dimensions.map((dvalue, index) =>
              <li key={index}><button className='dropdown-item' type="button" onClick={() => onTargetSelect(dvalue.name, dvalue.id, index)}>{dvalue.name}</button></li>
            )}
          </ul>
        </div>
      </td>)
    );
  }
}
const showAnalysisRun = (analysisRun, factCount, setAnalysisRun, setDimensions, setFactCount) => {
  if (analysisRun != null) {
    return (
      <tr><td>{analysisRun.populating ? <button className="btn btn-primary" onClick={() => handleRefreshClick(setDimensions, setAnalysisRun, setFactCount)}>Refresh</button> : <button className='btn btn-primary' onClick={() => handlePopulateClick(setAnalysisRun)}>Populate</button>}</td><td>{factCount}</td><td>{new Intl.DateTimeFormat('en-US', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit' }).format(analysisRun.lastRunDateTime)}</td><td>{String(analysisRun.populating)}</td></tr>
    );
  } else {
    return (
      <tr><td><button className="btn btn-primary" onClick={() => handlePopulateClick(setAnalysisRun)}>Populate</button></td><td>No Count</td><td>Not Run</td><td>FALSE</td></tr>
    );
  }
}
function olapResultDisplay(olapResult) {
  if (olapResult != null) {
    return (
      <div>
        <table className="table">
          <thead>
            <tr><th>Total</th><th>Mean</th><th>Min</th><th>Max</th><th>Std. Dev.</th></tr>
          </thead>
          <tbody>
            Fucked this up
          </tbody>
        </table>
        <table className="table">
          <thead>
            <tr><th>Amount</th><th>Project</th><th>Employee</th><th>Week</th><th>Hours Range</th><th>Rate Range</th></tr>
          </thead>
          <tbody>
            {olapResult.facts.map((fact, index) => <tr key={index}><td>{fact.amount.toLocaleString()}</td><td>{fact.project.name.toLocaleString()}</td><td>{fact.employee.name.toLocaleString()}</td><td>{fact.weekDimension.name.toLocaleString()}</td><td>{fact.hoursRangeDimension.name.toLocaleString()}</td><td>{fact.rateRangeDimension.name.toLocaleString()}</td></tr>)}
          </tbody>
        </table>
      </div>
    );
  }
}


export default function Analysis() {
  return <AnalysisPage />
};