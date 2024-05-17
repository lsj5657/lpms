import "./Settings.css";

const Settings = () => {
  return (
    <>
      <div className="Settings">
        <div className="SettingCheckBoxLine">
          <span className="SettingTitle">분석: </span>
          <div className="SettingCheckBoxAndName">
            <input type="checkbox" />
            <span className="Span">시간 이력 분석</span>
          </div>
          <div className="SettingCheckBoxAndName">
            <input type="checkbox" />
            <span>시간 - 주파수 분석</span>
          </div>
        </div>

        <div className="SettingCheckBoxLine">
          <span className="SettingTitle">화면 표시: </span>
          <div className="SettingCheckBoxAndName">
            <input type="checkbox" />
            <span>시간이력</span>
          </div>
          <div className="SettingCheckBoxAndName">
            <input type="checkbox" />
            <span>스펙트럼</span>
          </div>
          <div className="SettingCheckBoxAndName">
            <input type="checkbox" />
            <span>시간/스펙트럼 </span>
          </div>
          <div style={{ width: 10 }}></div>
          <span className="SettingTitle">Y축 설정: </span>
          <div className="SettingCheckBoxAndName">
            <input type="checkbox" />
            <span>Log</span>
          </div>
          <div className="SettingCheckBoxAndName">
            <input type="checkbox" />
            <span>Linear</span>
          </div>
        </div>
      </div>
    </>
  );
};

export default Settings;
