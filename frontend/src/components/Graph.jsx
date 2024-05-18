import React, { useEffect, useState } from "react";
import axios from "axios";
import {
  Chart as ChartJS,
  LinearScale,
  PointElement,
  LineElement,
  Tooltip,
  Legend,
} from "chart.js";
import { Line } from "react-chartjs-2";

// Chart.js에 필요한 컴포넌트 등록
ChartJS.register(LinearScale, PointElement, LineElement, Tooltip, Legend);

const Graph = () => {
  const [graphData, setGraphData] = useState([]);

  useEffect(() => {
    axios
      .get("/api/graph-data")
      .then((res) => {
        setGraphData(res.data);
      })
      .catch((error) => {
        console.error("Error fetching data:", error);
      });
  }, []);

  const data = {
    datasets: [
      {
        data: graphData.map((point) => ({ x: point.x, y: point.y })),
        fill: false,
        backgroundColor: "rgba(75,192,192,0.4)",
        borderColor: "rgba(75,192,192,1)",
      },
    ],
  };

  const options = {
    responsive: true,
    scales: {
      x: {
        type: "linear",
        position: "bottom",
      },
      y: {
        type: "linear",
        position: "left",
      },
    },
  };

  return (
    <div>
      = <Line data={data} options={options} />
    </div>
  );
};

export default Graph;
