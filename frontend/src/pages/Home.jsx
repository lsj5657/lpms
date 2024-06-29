// pages/Home.jsx

import Header from "../components/Header";
import EventLog from "../components/EventLog";
import { useContext } from "react";
import { ChannelInfo } from "../App";
import { ResultStatusContextController } from "../App";

const Home = () => {
  const data = useContext(ChannelInfo); // Retrieve channel information from context
  const setResultStatus = useContext(ResultStatusContextController); // Retrieve the function to set result status from context

  setResultStatus("NOTAVAILABLE"); // Set the result status to "NOTAVAILABLE"

  return (
    <>
      <Header text={"Event Log"} />
      <EventLog data={data} />
    </>
  );
};

export default Home;
