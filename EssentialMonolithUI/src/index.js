import React, { Fragment } from "react";
import ReactDOM from "react-dom";
import { Navbar, Nav } from "react-bootstrap";
import { BrowserRouter as Router, Route } from "react-router-dom";
import { withRouter } from "react-router";
import Home from "./Home";
import Planning from "./Planning";
import Analysis from "./Analysis";

const Header = props => {
  const { location } = props;
  return (
    <Navbar bg="light" variant="light">
      <Nav activeKey={location.pathname}>
        <Nav.Link href="/">Home</Nav.Link>
        <Nav.Link href="/planning">Planning</Nav.Link>
        <Nav.Link href="/analysis">Analysis</Nav.Link>
      </Nav>
    </Navbar>
  );
};
const HeaderWithRouter = withRouter(Header);

function App() {
  return (
    <div className="App">
      <Router>
        <Fragment>
          <HeaderWithRouter />
          <Route path="/" exact component={Home} />
          <Route path="/planning" exact component={Planning} />
          <Route path="/analysis" exact component={Analysis} />
        </Fragment>
      </Router>
    </div>
  );
}

const rootElement = document.getElementById("root");
ReactDOM.render(<App />, rootElement);
