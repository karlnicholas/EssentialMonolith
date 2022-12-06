import React, { Component } from "react";
import { Switch, Route, Link } from "react-router-dom";

import Home from "./Home";
import Planning from "./Planning";
import Analysis from "./Analysis";

class App extends Component {
  render() {
    return (
      <div>
        <nav className="navbar navbar-expand navbar-dark bg-dark">
          <Link to={"/"} className="navbar-brand">
            Home
          </Link>
          <div className="navbar-nav mr-auto">
            <li className="nav-item">
              <Link to={"/planning"} className="nav-link">
                Planning
              </Link>
            </li>
            <li className="nav-item">
              <Link to={"/analysis"} className="nav-link">
                Analysis
              </Link>
            </li>
          </div>
        </nav>

        <div className="container mt-3">
          <Switch>
            <Route exact path="/" component={Home} />
            <Route path="/planning" component={Planning} />
            <Route path="/analysis" component={Analysis} />
          </Switch>
        </div>
      </div>
    );
  }
}

export default App;