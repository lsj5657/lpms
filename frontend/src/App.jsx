// App.jsx

import "./App.css";
import Home from "./pages/Home";
import { Routes, Route } from "react-router-dom";
import { useState, useEffect, createContext } from "react";
import { useNavigate } from "react-router-dom";
import Channel from "./pages/Channel";
import axios from "axios";
import Info from "./components/Info";
import Button from "./components/Button";
import ShowImage from "./pages/ShowImage";

// Create contexts for sharing data between components
export const ChannelInfo = createContext();

const mockData = [
  {
    Time: "Loading",
    Status: "Loading",
    Remark: "Loading",
  },
];

export const ResultStatusContext = createContext();
export const ResultStatusContextController = createContext();
export const NowResultConntext = createContext();
export const NowResultConntextController = createContext();

function App() {
  const [darkMode, setDarkMode] = useState(true); // State to manage dark mode
  const nav = useNavigate(); // Hook for navigation
  const [data, setData] = useState(mockData); // State to store fetched data
  const [resultStatus, setResultStatus] = useState("NOTAVAILABLE"); // State to store result status
  const [nowResult, setNowResult] = useState(); // State to store current result

  // Effect to toggle dark/light mode based on darkMode state
  useEffect(() => {
    document.body.className = darkMode ? "dark-mode" : "light-mode";
  }, [darkMode]);

  // Function to toggle dark mode
  const toggleDarkMode = () => {
    setDarkMode((prevMode) => !prevMode);
  };

  // Effect to fetch data from the server when the component mounts
  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get("http://localhost:8080/api/files");
        const transformedData = response.data.map((item) => ({
          Time: item.time,
          // Channel: item.channel,
          Status: item.status,
          // Type: item.type,
          Remark: item.fileName,
        }));
        setData(transformedData); // Update state with fetched data
      } catch (error) {
        console.error("Error fetching data:", error); // Log any errors
      }
    };
    fetchData();
  }, []);

  return (
    <>
      <ResultStatusContext.Provider value={resultStatus}>
        <NowResultConntext.Provider value={nowResult}>
          <ResultStatusContextController.Provider value={setResultStatus}>
            <NowResultConntextController.Provider value={setNowResult}>
              <div id="root">
                <div className="App">
                  <div className="App_left">
                    <div onClick={() => nav("/")} className="title">
                      LPMS
                    </div>
                    <Routes>
                      <Route
                        path="/"
                        element={
                          <Button
                            onClick={toggleDarkMode}
                            text={darkMode ? "Light Mode" : "Dark Mode"}
                          />
                        }
                      ></Route>
                    </Routes>
                    <div>
                      <Routes>
                        <Route path="/channel/:Remark" element={<Info />} />
                      </Routes>
                    </div>
                  </div>
                  <div className="App_right">
                    <ChannelInfo.Provider value={data}>
                      <Routes>
                        <Route path="/" element={<Home />} />
                        <Route path="/channel/:Remark" element={<Channel />} />
                        <Route path="/showimage" element={<ShowImage />} />
                      </Routes>
                    </ChannelInfo.Provider>
                  </div>
                </div>
              </div>
            </NowResultConntextController.Provider>
          </ResultStatusContextController.Provider>
        </NowResultConntext.Provider>
      </ResultStatusContext.Provider>
    </>
  );
}

export default App;
