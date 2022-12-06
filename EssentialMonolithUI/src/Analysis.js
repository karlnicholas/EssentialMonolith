import React, {useState, useEffect, useRef} from 'react';
import http from "./http-common";

function AnalysisPage() {
  const [analysisRun, setAnalysisRun] = useState(null);
  const [dimensions, setDimensions] = useState([]);
  const [olapResult, setOlapResult] = useState(null);
  const [whereSelects, setWhereSelects] = useState([]);
  const [factCount, setFactCount] = useState(0);
  const dropdownTitles = useRef([]);
  const dropdownIds = useRef([]);

  useEffect(() => {
    http.get('/analysis').then(response => {
      setDimensions(response.data.analysisDimensions);
      setAnalysisRun(response.data.analysisRun);
      setFactCount(response.data.factCount);
      updateDimensions(response.data.analysisDimensions);
      });
  }, []);

  const updateDimensions = (dimensions) => {
    dropdownTitles.current = new Array(dimensions.length).fill("");
    dropdownIds.current = new Array(dimensions.length).fill(null);
  };


  const showAnalysisRun = () => {
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
  const tableHeaders = () => {
    if (dimensions.length > 0) {
      return (
        dimensions.map(dimension => <th key={dimension.name}>{dimension.name}</th>)
      );
    }
  };
  const tableBody = () => {
    if (dimensions.length > 0) {
      return (dimensions.map((dlist, index) =>
        <td key={dlist.name}>
          <div key={index} className="dropdown">
            <button className='btn btn-primary dropdown-toggle' type="button" data-bs-toggle="dropdown" aria-expanded="false" id={dlist.name}>{dimensions[index].title !== undefined ? dimensions[index].title : ''}</button >
            <ul className="dropdown-menu">
              <li key={index}><button className='dropdown-item' type="button" onClick={() => onTargetSelect("", null, index)}>&nbsp;</button></li>
              {dlist.dimensions.map((dvalue, idx) =>
                <li key={idx}><button className='dropdown-item' type="button" onClick={() => onTargetSelect(dvalue.name, dvalue.id, index)}>{dvalue.name}</button></li>
              )}
            </ul>
          </div>
        </td>)
      );
    }
  }
  const card = () => {
    if (dimensions.length > 0) {
      return (dimensions.map((dlist, index) =>
        <div key={index} className="dropdown">
          <button className="btn btn-primary dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false" key={dlist.name} id={dlist.name}>{dlist.name}</button >
          <ul className="dropdown-menu">
            {dlist.dimensions.map((dvalue, index) =>
              <li key={index}><button className="dropdown-item" type='button' onClick={() => onWhereSelect(dimensions[index].name, dvalue.name, dvalue.id, whereSelects, setWhereSelects)}>{dvalue.name}</button></li>
            )}
          </ul>
        </div>
      ));
    }
  }
  const handlePopulateClick = () => {
    http.get('/analysis/populate').then(response => {
      setAnalysisRun(response.data);
    });
  }
  const handleRefreshClick = () => {
    http.get('/analysis').then(response => {
      setDimensions(response.data.analysisDimensions);
      setAnalysisRun(response.data.analysisRun);
      setFactCount(response.data.factCount);
      updateDimensions(response.data.analysisDimensions);
    });
  }
  const onTargetSelect = (name, id, index) => {
    let titles = dropdownTitles.current.slice(); //creates the clone of the state
    titles[index] = name;
    let ids = dropdownIds.current.slice(); //creates the clone of the state
    ids[index] = id;
    dropdownTitles.current = titles;
    dropdownIds.current = ids;
  };

  const onQueryClick = () => {
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
  return (
    <div>
      <table className="table">
        <thead>
          <tr><th>Populate</th><th>Fact Count</th><th>Last Run Time</th><th>Populating</th></tr>
        </thead>
        <tbody>
          {showAnalysisRun()}
        </tbody>
      </table>
      <div>
        {card()}
      </div>
      <table className="table">
        <thead>
          <tr><th>Query</th>{tableHeaders()}</tr>
        </thead>
        <tbody>
          <tr><td><button className="btn btn-primary" onClick={() => onQueryClick(dimensions, setOlapResult)}>Query</button></td>{tableBody()}</tr>
        </tbody>
      </table>
      {olapResultDisplay(olapResult)}
    </div>
  );
}

function onWhereSelect(dname, name, id, whereSelects, setWhereSelects) {
  let ws = whereSelects.slice(); //creates the clone of the state
  ws.push({ 'display': dname + ':' + name + ":" + id, 'property': dname, 'id': id });
  setWhereSelects(ws);
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