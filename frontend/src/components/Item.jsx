// component/Item.jsx
// Used to maintain consistent spacing between attributes

import "./Item.css";

const Item = ({ text, attribute }) => {
  return (
    <>
      <div className="Item">
        <div className={`Item Item_${attribute}`}>{text}</div>
      </div>
    </>
  );
};
export default Item;
