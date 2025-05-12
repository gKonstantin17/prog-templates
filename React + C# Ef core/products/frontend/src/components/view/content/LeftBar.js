import {LeftBarElement} from "./leftBar/LeftBarElement";

function LeftBar({tabs,activeTab,setActiveTab}) {
    const activeTabIndex = tabs.findIndex(tab => tab.id === activeTab);

    return (<div className="left-bar">
        <div className="tab-container">
            {tabs.map(tab => (
                <LeftBarElement key={tab.id}
                                title={tab.name}
                                isActive={activeTab === tab.id}
                                onClick={() => setActiveTab(tab.id)}
                />
            ))}
            <div className="indicator"
                 style={{
                     top: `${activeTabIndex * 41 + 4}px`
                 }}></div>
            <div className="scroller"
                 style={{
                     height: `${tabs.length * 40}px` // например, 40px на каждый tab
                 }}></div>
        </div>

    </div>)
}
export {LeftBar}