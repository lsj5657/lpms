// pages/ShowImage.jsx

import React from "react";
import { useLocation } from "react-router-dom";
import "./ShowImage.css";

const ShowImage = () => {
  const location = useLocation(); // Hook to access the location object, which contains the state
  const { imagePath } = location.state || {}; // Destructure imagePath from the location state

  return (
    <div className="ShowImage">
      <div className="Title">AI True/False Determination</div>{" "}
      {imagePath ? ( // Check if imagePath exists
        <div className="Image-wrapper">
          <img src={imagePath} alt="Full View" className="Full-image" />{" "}
        </div>
      ) : (
        <p>No image to display</p> // Display a message if imagePath is not available
      )}
    </div>
  );
};

export default ShowImage;
