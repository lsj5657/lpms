import React, { useState } from "react";
import "./App.css";
import Header from "./components/Header";
import GraphControllers from "./components/GraphControllers";
import LoadFile from "./components/LoadFile";
import InfoFile from "./components/InfoFile";
import Steps from "./components/Steps";
import Settings from "./components/Settings";
import Graph from "./components/Graph";

function App() {
  const [zoomOption, setZoomOption] = useState(null);

  const handleZoom = (option) => {
    setZoomOption(option);
  };

  return (
    <div className="App">
      <Header />
      <div>
        <GraphControllers handleZoom={handleZoom} />
        <div className="View">
          <div className="ViewLeft">
            <InfoFile />
            <LoadFile />
          </div>
          <div className="ViewRight">
            <Steps />
            <Settings />
            <Graph zoomOption={zoomOption} />
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;
