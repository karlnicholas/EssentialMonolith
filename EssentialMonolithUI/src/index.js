import * as ReactDOM from 'react-dom/client';
import { BrowserRouter } from "react-router-dom";

import App from "./App";

const container = document.getElementById('root');

// Create a root.
const root = ReactDOM.createRoot(container);

// Initial render
root.render(
  <BrowserRouter>
    <App />
  </BrowserRouter>
);