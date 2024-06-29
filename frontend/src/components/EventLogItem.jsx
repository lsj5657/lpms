// components/EventLogItem.jsx

import { useNavigate } from "react-router-dom";
import { useContext } from "react";
import {
  NowResultConntextController,
  ResultStatusContextController,
} from "../App";
import "./EventLogItem.css";
import Item from "./Item";

// EventLogItem component

const EventLogItem = ({ Time, Channel, Status, Type, Remark }) => {
  const nav = useNavigate(); // Hook for navigation
  const setNowFile = useContext(NowResultConntextController); // Context for the selected file
  const setResultStatus = useContext(ResultStatusContextController); // Context for the result status

  const handleClick = () => {
    setNowFile({ Time, Channel, Status, Type, Remark }); // Set the current file with the provided details
    setResultStatus("AVAILABLE"); // Set the result status to "AVAILABLE"
    nav(`channel/${Remark}`); // Navigate to the channel page with the remark as the route parameter
  };

  let statusText;
  if (Status === "Loading") {
    // default status = "Loading"
    statusText = "Loading"; // If status is "Loading", set the status text to "Loading"
  } else {
    statusText = Status ? "True" : "False"; // Otherwise, set it to "True" or "False" based on the status
  }

  return (
    <>
      <div className="EventLogItem" onClick={handleClick}>
        <Item text={Time} attribute={"Date"} /> {/* Display the time */}
        {/* <Item text={Channel} attribute={"Channel"} /> */}
        {/* Display the channel */}
        <Item text={statusText} attribute={"Status"} />
        {/* Display the status */}
        <Item text={Remark} attribute={"Remark"} />
        {/* Display the remark */}
      </div>
    </>
  );
};

export default EventLogItem;
