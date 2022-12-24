import React, {useState, useEffect} from 'react';
import http from "./http-common";

function newQueryParams(length) {
  return new Array(length).fill(0).map(() => ({
    title: '', 
    id: null
  }));
}

function AnalysisPage() {
  const [analysisRun, setAnalysisRun] = useState(null);
  const [dimensions, setDimensions] = useState([]);
  const [olapResult, setOlapResult] = useState(null);
  const [whereSelects, setWhereSelects] = useState([]);
  const [groupBySelects, setGroupBySelects] = useState([]);
  const [factCount, setFactCount] = useState(0);
  const [queryParams, setQueryParams] = useState([]);

  useEffect(() => {
    http.get('/analysis').then(response => {
      setDimensions(response.data.analysisDimensions);
      setAnalysisRun(response.data.analysisRun);
      setFactCount(response.data.factCount);
      setQueryParams(newQueryParams(response.data.analysisDimensions.length));
    });
  }, []);

  useEffect(() => {
    var olapQuery = {};
    olapQuery.whereList = [];
    olapQuery.groupByList = [];
    if (whereSelects.length > 0) {
      var i;
      for (i = 0; i < whereSelects.length; ++i) {
        olapQuery.whereList.push({'property': lowercaseFirst(whereSelects[i].property), 'id': whereSelects[i].id});
      }
    }
    if (groupBySelects.length > 0) {
      for (i = 0; i < groupBySelects.length; ++i) {
        olapQuery.groupByList.push(lowercaseFirst(groupBySelects[i].property));
      }
    }
    if ( olapQuery.whereList.length > 0 || olapQuery.groupByList.length > 0) {
      var request = {};
      request.olapQuery = olapQuery;
      (async()=>{
        http.post('/analysis/olap', olapQuery).then(response => {
          setOlapResult(response.data);
        })
      })();
      }
  }, [whereSelects, groupBySelects]);


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
  }

  const tableBody = () => {
    if (dimensions.length > 0) {
      return (dimensions.map((dlist, index) =>
        <td key={dlist.name}>
          <div key={index} className="dropdown">
            <button className='btn btn-primary dropdown-toggle' type="button" data-bs-toggle="dropdown" aria-expanded="false" id={dlist.name}>{queryParams[index].title}</button >
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
      setQueryParams(newQueryParams(response.data.analysisDimensions.length));
    });
  }

  const onTargetSelect = (name, id, index) => {
    let params = queryParams.slice(); //creates the clone of the state
    params[index].title = name;
    params[index].id = id;
    setQueryParams(params);
  }

  const onQueryClick = () => {
    var queryString = '';
    var queryMark = '?';
    var i;
    for (i = 0; i < queryParams.length; ++i) {
      var select = queryParams[i];
      if (select.id !== null) {
        queryString += queryMark + dimensions[i].name + '=' + select.id;
        queryMark = '&';
      }
    }
    (async () => {
      http.get('/analysis/billingresult' + queryString).then(response => {
        setOlapResult(response.data);
      })
    })();
  };

  const olapResultDisplay = () => {
    if (olapResult != null) {
      return (
        <div>
          <table className="table">
            <thead>
              <tr><th>Total</th><th>Mean</th><th>Min</th><th>Max</th><th>Std. Dev.</th></tr>
            </thead>
            <tbody>
            <tr><td>{olapResult.summaryStatistics.sum.toLocaleString()}</td><td>{olapResult.summaryStatistics.mean.toLocaleString()}</td><td>{olapResult.summaryStatistics.min.toLocaleString()}</td><td>{olapResult.summaryStatistics.max.toLocaleString()}</td><td>{olapResult.summaryStatistics.standardDeviation.toLocaleString()}</td></tr>
            </tbody>
          </table>
          <table className="table">
            <thead>
              <tr><th>Amount</th><th>Project</th><th>Employee</th><th>Week</th><th>Hours Range</th><th>Rate Range</th></tr>
            </thead>
            <tbody>
              {olapResult.facts.map((fact, index) => <tr key={index}><td>{fact.amount.toLocaleString()}</td><td>{fact.project.name.toLocaleString()}</td><td>{fact.employee.name.toLocaleString()}</td><td>{fact.week.name.toLocaleString()}</td><td>{fact.hoursRange.name.toLocaleString()}</td><td>{fact.rateRange.name.toLocaleString()}</td></tr>)}
            </tbody>
          </table>
        </div>
      );
    }
  }  
  const lowercaseFirst = (str) => `${str.charAt(0).toLowerCase()}${str.slice(1)}`;
  const whereDropdowns = () => {
    if (dimensions.length > 0) {
      return (dimensions.map((dlist, index) =>
        <div key={index} className="dropdown">
          <button className="btn btn-primary dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false" key={dlist.name} id={dlist.name}>{dlist.name}</button >
          <ul className="dropdown-menu">
            {dlist.dimensions.map((dvalue, idx) =>
              <li key={idx}><button className="dropdown-item" type='button' onClick={() => onWhereSelect(dimensions[index].name, dvalue.name, dvalue.id)}>{dvalue.name}</button></li>
            )}
          </ul>
        </div>
      ));
    }
  }
  const onWhereSelect = (dname, name, id) => {
    let ws = whereSelects.slice(); //creates the clone of the state
    const fi = ws.findIndex(e => (e.property === dname && e.id === id));
    if ( fi < 0 ) {
      ws.push({ display: dname + ':' + name, property: dname, id: id });
    } else {
      ws.splice(fi, 1);
    }
    setWhereSelects(ws);
  }

  const showWhereSelects = () => {
    const buttons = whereSelects.map((s, i) => 
        <button key={i} type='button' className='btn btn-primary text-nowrap'>{s.display}</button>
    );
    return (buttons);
  }

  const groupByDropdowns = () => {
    if (dimensions.length > 0) {
      return (dimensions.map((dlist, index) =>
        <div key={index} className="dropdown">
          <button className="btn btn-primary dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false" key={dlist.name} id={dlist.name}>{dlist.name}</button >
          <ul className="dropdown-menu">
            {dlist.dimensions.map((dvalue, idx) =>
              <li key={idx}><button className="dropdown-item" type='button' onClick={() => onGroupBySelect(dimensions[index].name, dvalue.name, dvalue.id)}>{dvalue.name}</button></li>
            )}
          </ul>
        </div>
      ));
    }
  }
  const onGroupBySelect = (dname, name, id) => {
    let gs = groupBySelects.slice(); //creates the clone of the state
    const fi = gs.findIndex(e => (e.property === dname && e.id === id));
    if ( fi < 0 ) {
      gs.push({ display: dname + ':' + name, property: dname, id: id });
    } else {
      gs.splice(fi, 1);
    }
    setGroupBySelects(gs);
  }

  const showGroupBySelects = () => {
    const buttons = groupBySelects.map((s, i) => 
        <button key={i} type='button' className='btn btn-primary text-nowrap'>{s.display}</button>
    );
    return (buttons);
  }

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
      <div className="row justify-content-start">
        <div className="card" style={{width:'10rem'}}>
          <div className="card-body">
            <h6 className="card-title">Select</h6>
            {whereDropdowns()}
          </div>
        </div>
        <div className="card" style={{width:'14rem'}}>
          <div className="card-body">
            <h6 className="card-title">Selected</h6>
            {showWhereSelects()}
          </div>
        </div>
        <div className="card" style={{width:'10rem'}}>
          <div className="card-body">
            <h6 className="card-title">Group</h6>
            {groupByDropdowns()}
          </div>
        </div>
        <div className="card" style={{width:'14rem'}}>
          <div className="card-body">
            <h6 className="card-title">Grouped</h6>
            {showGroupBySelects()}
          </div>
        </div>
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

export default function Analysis() {
  return <AnalysisPage />
};