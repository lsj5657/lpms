import React, { useRef, useEffect, useState, useContext } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  Chart as ChartJS,
  LinearScale,
  PointElement,
  LineElement,
  Tooltip,
  Legend,
  LogarithmicScale,
} from "chart.js";
import { NowResultConntext } from "../App";
import "./Channel.css";
import axios from "axios";
import { Line } from "react-chartjs-2";

ChartJS.register(
    LinearScale,
    PointElement,
    LineElement,
    Tooltip,
    Legend,
    LogarithmicScale
);

// Graph options
const graphoptions = {
  responsive: true,
  plugins: {
    legend: {
      display: false,
    },
    tooltip: {
      enabled: false,
    },
  },
  scales: {
    x: {
      type: "linear",
      position: "bottom",
    },
    y: {
      type: "linear", // default type = linear
      position: "left",
    },
  },
};

// Function to translate data for the graph
const dataTranslate = (lowData) => {
  if (!lowData || lowData.length === 0) return { datasets: [] };

  const highData = {
    datasets: [
      {
        data: lowData.map((point) => ({ x: point.x, y: point.y })),
        fill: false,
        backgroundColor: "rgba(75,192,192,0.4)",
        borderColor: "rgba(75,192,192,1)",
        pointRadius: 0.5,
        borderWidth: 0.5,
      },
    ],
  };
  return highData;
};

// Channel options for the dropdown
const channeloptions = [
  { value: 0, label: "V101" },
  { value: 1, label: "V102" },
  { value: 2, label: "V103" },
  { value: 3, label: "V104" },
  { value: 4, label: "V105" },
  { value: 5, label: "V106" },
  { value: 6, label: "V107" },
  { value: 7, label: "V108" },
  { value: 8, label: "V109" },
  { value: 9, label: "V110" },
  { value: 10, label: "V111" },
  { value: 11, label: "V112" },
  { value: 12, label: "V113" },
  { value: 13, label: "V114" },
  { value: 14, label: "V115" },
  { value: 15, label: "V116" },
  { value: 16, label: "V117" },
  { value: 17, label: "V118" },
];

// Function to find the value by label in the channel options
const findValueByLabel = (label) => {
  const option = channeloptions.find((option) => option.label === label);
  return option ? option.value : null;
};

const Channel = () => {
  const params = useParams(); // Get URL parameters
  const navigate = useNavigate(); // Hook for navigation
  const nowResult = useContext(NowResultConntext); // Get context data
  const canvasRef = useRef(null); // Reference for the canvas
  const [selectedCh, setSelectedCh] = useState(findValueByLabel("V101")); // State for selected channel
  const [isLogScale, setIsLogScale] = useState(false); // State for log scale toggle
  const [originalImagePath, setOriginalImagePath] = useState(""); // State for the original image path

  const [TimeData, setGraphTimeData] = useState([]); // State for time data
  const [FrequencyData, setGraphFrequencyData] = useState([]); // State for frequency data

  const [data, setData] = useState({
    imagePath: "",
    aiResult: "",
    aiProbability: "",
    showResultPage: false,
  }); // State for result data

  // Update selected channel when URL parameter changes
  useEffect(() => {
    const updateSelectedCh = () => {
      setSelectedCh(findValueByLabel(nowResult.Channel));
    };
    updateSelectedCh();
  }, [nowResult.Channel]);

  // Fetch time data
  useEffect(() => {
    axios
        .get(`/api/time/${params.Remark}?selectedCh=${selectedCh}`)
        .then((res) => {
          setGraphTimeData(res.data);
        })
        .catch((error) => {
          console.error("Error fetching time data:", error);
          setGraphTimeData([]);
        });
  }, [params.Remark, selectedCh]);

  // Fetch frequency data
  useEffect(() => {
    axios
        .get(`/api/freq/${params.Remark}?selectedCh=${selectedCh}`)
        .then((res) => {
          setGraphFrequencyData(res.data);
        })
        .catch((error) => {
          console.error("Error fetching frequency data:", error);
          setGraphFrequencyData([]);
        });
  }, [params.Remark, selectedCh]);

  // Handle button click to get AI result
  const handleButtonClick = () => {
    axios
        .get(`/api/result/${nowResult.Remark}`, { params: { selectedCh } })
        .then((res) => {
          console.log(res.data);
          const temdata = {
            aiResult: res.data.aiResult,
            aiProbability: res.data.aiProbability,
            showResultPage: true,
            imagePath: res.data.imagePath,
          };
          setOriginalImagePath(res.data.imagePath); // Store original image path
          setData((prevData) => {
            const newData = {
              ...prevData,
              ...temdata,
            };
            console.log(newData);
            return newData;
          });
        })
        .catch((error) => {
          console.error("Error fetching result data:", error);
        });
  };

  // Log the data state when it changes
  useEffect(() => {
    console.log(data);
  }, [data]);

  // Toggle between logarithmic and linear scale
  const toggleLogScale = () => {
    setIsLogScale(!isLogScale);
  };

  // Options for the frequency graph
  const graphFrequencyOptions = {
    ...graphoptions,
    scales: {
      ...graphoptions.scales,
      y: {
        ...graphoptions.scales.y,
        type: isLogScale ? "logarithmic" : "linear",
      },
    },
  };

  // Function to resize image
  const resizeImage = (imageSrc, callback) => {
    const img = new Image();
    img.src = imageSrc;
    img.onload = () => {
      const canvas = canvasRef.current;
      if (canvas) {
        const ctx = canvas.getContext("2d");
        canvas.width = 300;
        canvas.height = 300;
        ctx.drawImage(img, 0, 0, 300, 300);
        const resizedImageURL = canvas.toDataURL();
        callback(resizedImageURL);
      }
    };
  };

  // Resize the image when the imagePath in data state changes
  useEffect(() => {
    if (data.imagePath) {
      resizeImage(data.imagePath, (resizedImage) =>
          setData((prevData) => ({
            ...prevData,
            imagePath: resizedImage,
          }))
      );
    }
  }, [data.imagePath]);

  // Get CSS class based on result status
  const getResultClass = (s) => {
    if (s === "AVAILABLE") return "available";
    if (s === "True") return "true";
    if (s === "False") return "false";
    return "";
  };

  // Handle image click to navigate to showimage page
  const handleImageClick = () => {
    navigate("/showimage", { state: { imagePath: originalImagePath } }); // Use original image path
  };

  return (
      <>
        <div className="Channel">
          <div className="Top">
            <div className="LeftSide">
              <select
                  className="select"
                  value={selectedCh}
                  onChange={(e) => setSelectedCh(Number(e.target.value))}
              >
                {channeloptions.map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                ))}
              </select>
              <div>Filename: {params.Remark}</div>
            </div>
            <button onClick={handleButtonClick} className="Button">
              Get Result
            </button>
          </div>
          <div className="Graphs">
            <div className="Graph">
              <div className="Title">TIME</div>
              <div>
                <Line data={dataTranslate(TimeData)} options={graphoptions} />
              </div>
            </div>
            <div className="Graph">
              <div className="Title">
                <div>FREQUENCY</div>
                <button className="LogLinear" onClick={toggleLogScale}>
                  {isLogScale ? "Log" : "Linear"}
                </button>
              </div>

              <div>
                <Line
                    data={dataTranslate(FrequencyData)}
                    options={graphFrequencyOptions}
                />
              </div>
            </div>
          </div>
        </div>
        {data.showResultPage &&
            data.imagePath &&
            data.aiResult !== "" &&
            data.aiProbability !== "" && (
                <div className="ResultImage">
                  <canvas ref={canvasRef} style={{ display: "none" }} />
                  <div style={{ cursor: "pointer" }}>
                    <img
                        src={data.imagePath}
                        alt="Result"
                        onClick={handleImageClick}
                    />
                  </div>
                  <div className="Text">
                    <div>AI Result for file {nowResult.Remark}:</div>
                    <div
                        className={`result ${getResultClass(
                            data.aiResult ? "True" : "False"
                        )}`}
                    >
                      Result: {data.aiResult ? "True" : "False"}
                    </div>
                    <div className={`result available`}>
                      Impact Probability: {(data.aiProbability * 100).toFixed(5)}%
                    </div>
                  </div>
                </div>
            )}
      </>
  );
};

export default Channel;
