// components/EventLog.jsx

import { useState } from "react";
import EventLogItem from "./EventLogItem";
import "./EventLog.css";
import Item from "./Item";

const EventLog = ({ data }) => {
  // Sort Type
  const [timeFilter, setTimeFilter] = useState("latest"); // by time
  const [statusFilter, setStatusFilter] = useState("all"); // by Status
  // const [typeFilter, setTypeFilter] = useState("all"); // by Type

  // Select box onChange handler
  const onChangeTimeFilter = (e) => {
    setTimeFilter(e.target.value);
  };

  const onChangeStatusFilter = (e) => {
    setStatusFilter(e.target.value);
  };

  // const onChangeTypeFilter = (e) => {
  //   setTypeFilter(e.target.value);
  // };

  // Translate String date to Time date
  // ex) 2089-06-13 19:50:22.511
    const parseDateString = (dateString) => {
        const parts = dateString.split(/[- :]/); // split by "- :"
        // Check if the parts length is 6 or 7 (with or without milliseconds)
        const dateTime = new Date(
            parts[0],
            parts[1] - 1, // in JavaScript, Month starts with 0
            parts[2],
            parts[3],
            parts[4],
            parts[5]
        );
        return dateTime;
    };


    // Sort data by Filter
  const getSortedData = () => {
    return data
      .slice() // a copy of the original array to avoid mutating it
      .sort((a, b) => {
        const date1 = parseDateString(a.Time); // Convert Time string of a to Date object
        const date2 = parseDateString(b.Time); // Convert Time string of b to Date object
        if (timeFilter === "latest") {
          // Sort by latest
          return date1 > date2 ? -1 : 1; // Return -1 if date1 is greater, otherwise return 1 (descending order)
        } else {
          // Sort by oldest
          return date1 < date2 ? -1 : 1; // Return -1 if date1 is lesser, otherwise return 1 (descending order)
        }
      })
      .filter((item) => {
        // Filter the data based on conditions
        // If statusFilter is not "all" and item.Status doesn't match statusFilter, return false
        if (
          statusFilter !== "all" &&
          (item.Status ? "True" : "False") !== statusFilter
        ) {
          return false;
        }
        // If typeFilter is not "all" and item.Type doesn't match typeFilter, return false
        // if (typeFilter !== "all" && item.Type !== typeFilter) {
        //   return false;
        // }
        // If all conditions are passed, return true
        return true;
      });
  };

  const sortedData = getSortedData();

  return (
    <div className="EventLog">
      <div className="menu_bar">
        <select value={timeFilter} onChange={onChangeTimeFilter}>
          <option value={"latest"}>LATEST</option>
          <option value={"oldest"}>OLDEST</option>
        </select>
        <select value={statusFilter} onChange={onChangeStatusFilter}>
          <option value={"all"}>ALL</option>
          <option value={"True"}>TRUE</option>
          <option value={"False"}>FALSE</option>
        </select>
        {/* <select value={typeFilter} onChange={onChangeTypeFilter}>
          <option value={"all"}>ALL</option>
          <option value={"Dual"}>DUAL</option>
          <option value={"Fixed"}>FIXED</option>
          <option value={"Floating"}>FLOATING</option>
          <option value={"NA"}>NA</option>
        </select> */}
      </div>
      <div className="menu_name">
        <Item text={"Date"} attribute={"Date"} />
        {/* <Item text={"Event Channel"} attribute={"Channel"} /> */}
        <Item text={"Status"} attribute={"Status"} />
        {/* <Item text={"Type"} attribute={"Type"} /> */}
        <Item text={"Remark"} attribute={"Remark"} />
      </div>
      <div className="list_wrapper">
        {sortedData.map((item) => (
          <EventLogItem key={item.Remark} {...item} />
        ))}
      </div>
    </div>
  );
};

export default EventLog;
