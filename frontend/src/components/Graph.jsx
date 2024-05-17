import React, { useEffect, useState } from "react";
import axios from "axios";

const Graph = () => {
  const [hello, setHello] = useState("");

  useEffect(() => {
    axios.get("/api/test").then((res) => {
      setHello(res.data);
    });
  }, []);
  return <div className="App">백엔드 데이터 : {hello}</div>;
};

export default Graph;
