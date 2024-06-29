// component/Info.jsx
// show information of selected File

import { NowResultConntext } from "../App";
import "./Info.css";
import { useContext } from "react";

const Info = () => {
  const nowResult = useContext(NowResultConntext);

  return (
    <>
      <div className="Info">
        <text>Date: {nowResult.Time}</text>
        {/* <text>Event Channel: {nowResult.Channel}</text> */}
        <text>Status: {nowResult.Status ? "True" : "False"}</text>
        {/* <text>Type: {nowResult.Type}</text> */}
      </div>
    </>
  );
};

export default Info;
