// components/Button.jsx

import "./Button.css";

const Button = ({ onClick, type, text }) => {
  return (
    <>
      <div className="Buttons">
        <button
          onClick={onClick} // onClick handler
          type={type} // POSITIVE type, NAGATIVE type
          className={`Button Button_${type}`}
        >
          {text}
        </button>
      </div>
    </>
  );
};
export default Button;
