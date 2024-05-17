import React from "react";

import "./GraphControllers.css";

const GraphControllers = ({ handleZoom }) => {
  return (
    <div className="zoom_wrapper">
      <button onClick={() => handleZoom("x")}>Zoom X</button>
      <button onClick={() => handleZoom("y")}>Zoom Y</button>
      <button onClick={() => handleZoom("xy")}>Zoom X Y</button>
      <button onClick={() => handleZoom("undo")}>Zoom Undo</button>
      <button onClick={() => handleZoom("reset")}>Zoom Reset</button>
    </div>
  );
};

export default GraphControllers;
