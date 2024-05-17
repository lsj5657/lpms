import "./LoadFile.css";

const LoadFile = () => {
  return (
    <>
      <div className="LoadFile">
        <span className="LoadFileTitle">파일 선택</span>
        <div className="LoadFileBox1">
          <div className="LoadFilePath">
            <span className="LoadFileTitle">- 경로 : </span>
            <button className="LoadFileButton">변경</button>
          </div>
          <span className="LoadFileTitle">파일명</span>
        </div>
      </div>
    </>
  );
};
export default LoadFile;
